class MarketplaceApp {
    constructor() {
        this.cartId = localStorage.getItem('cartId') || null;
        this.products = [];
        this.cart = null;
        this.currentProduct = null;
        
        this.initializeElements();
        this.attachEventListeners();
        this.loadProducts();
        
        if (this.cartId) {
            this.loadCart();
        }
    }

    initializeElements() {
        this.productsList = document.getElementById('productsList');
        this.cartSection = document.getElementById('cartSection');
        this.productsSection = document.getElementById('productsSection');
        this.cartItems = document.getElementById('cartItems');
        this.cartCount = document.getElementById('cartCount');
        this.notification = document.getElementById('notification');
        
        this.viewCartBtn = document.getElementById('viewCartBtn');
        this.checkoutBtn = document.getElementById('checkoutBtn');
        this.backToProductsBtn = document.getElementById('backToProductsBtn');
        this.productDetailSection = document.getElementById('productDetailSection');
        this.productDetail = document.getElementById('productDetail');
        this.backToProductsFromDetail = document.getElementById('backToProductsFromDetail');
    }

    attachEventListeners() {
        this.viewCartBtn.addEventListener('click', () => this.showCart());
        this.checkoutBtn.addEventListener('click', () => this.checkout());
        this.backToProductsBtn.addEventListener('click', () => this.showProducts());
        
        if (this.backToProductsFromDetail) {
            this.backToProductsFromDetail.addEventListener('click', (e) => {
                e.preventDefault();
                this.showProducts();
            });
        }
    }

    async loadProducts() {
        try {
            this.productsList.innerHTML = '<div class="loading">Loading products...</div>';
            
            const response = await fetch('/api/products');
            if (!response.ok) throw new Error('Failed to load products');
            
            this.products = await response.json();
            this.renderProducts();
        } catch (error) {
            this.showNotification('Failed to load products', 'error');
            this.productsList.innerHTML = '<div class="loading">Failed to load products</div>';
        }
    }

    renderProducts() {
        if (this.products.length === 0) {
            this.productsList.innerHTML = '<div class="loading">No products available</div>';
            return;
        }

        this.productsList.innerHTML = this.products.map(product => `
            <div class="product-card">
                <div class="product-name">${this.escapeHtml(product.name)}</div>
                <div class="product-description">${this.escapeHtml(product.description)}</div>
                <div class="product-price">$${product.price.toFixed(2)}</div>
                <button class="add-to-cart-btn" onclick="app.addToCart('${product.id}')">
                    Add to Cart
                </button>
                <button class="view-details-btn" onclick="app.showProductDetail('${product.id}')">
                    View Details
                </button>
            </div>
        `).join('');
    }

    async addToCart(productId) {
        try {
            if (!this.cartId) {
                await this.createCart();
            }

            const response = await fetch(`/api/carts/${this.cartId}/add-product`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ productId })
            });

            if (!response.ok) throw new Error('Failed to add product to cart');

            this.cart = await response.json();
            this.updateCartCount();
            this.showNotification('Product added to cart!', 'success');
        } catch (error) {
            this.showNotification('Failed to add product to cart', 'error');
        }
    }

    async createCart() {
        try {
            const response = await fetch('/api/carts', { method: 'POST' });
            if (!response.ok) throw new Error('Failed to create cart');

            this.cart = await response.json();
            this.cartId = this.cart.id;
            localStorage.setItem('cartId', this.cartId);
        } catch (error) {
            throw new Error('Failed to create cart');
        }
    }

    async loadCart() {
        try {
            const response = await fetch(`/api/carts/${this.cartId}`);
            if (!response.ok) {
                if (response.status === 404) {
                    this.cartId = null;
                    localStorage.removeItem('cartId');
                    this.cart = null;
                    this.updateCartCount();
                    return;
                }
                throw new Error('Failed to load cart');
            }

            this.cart = await response.json();
            this.updateCartCount();
        } catch (error) {
            this.showNotification('Failed to load cart', 'error');
        }
    }

    async showProductDetail(productId) {
        try {
            this.productDetail.innerHTML = '<div class="loading">Loading product details...</div>';
            this.hideAllSections();
            this.productDetailSection.classList.remove('hidden');

            const response = await fetch(`/api/products/${productId}`);
            if (!response.ok) {
                if (response.status === 404) {
                    this.productDetail.innerHTML = `
                        <div class="error-page">
                            <div class="error-icon">🔍</div>
                            <h3>Product Not Found</h3>
                            <p>The product you're looking for doesn't exist or has been removed.</p>
                            <button class="retry-btn" onclick="app.showProducts()">Back to Products</button>
                        </div>
                    `;
                    return;
                }
                throw new Error('Failed to load product');
            }

            this.currentProduct = await response.json();
            this.renderProductDetail();
        } catch (error) {
            console.error('Error loading product detail:', error);
            this.showNotification('Failed to load product details', 'error');
            this.productDetail.innerHTML = `
                <div class="error-page">
                    <h3>Error Loading Product</h3>
                    <p>Unable to load product details. Please try again.</p>
                    <button class="retry-btn" onclick="app.showProducts()">Back to Products</button>
                </div>
            `;
        }
    }

    renderProductDetail() {
        const product = this.currentProduct;
        const stockStatus = this.getStockStatus(product.stock);
        const isOutOfStock = product.stock === 0;

        this.productDetail.innerHTML = `
            <div class="product-detail-header">
                <div class="product-image-placeholder">📦</div>
                <div class="product-main-info">
                    <div class="product-detail-name">${this.escapeHtml(product.name)}</div>
                    <div class="product-detail-price">$${this.formatPrice(product.price)}</div>
                    <div class="stock-info ${stockStatus.class}">${stockStatus.message}</div>
                </div>
            </div>
            
            <div class="product-detail-description">${this.escapeHtml(product.description)}</div>
            
            ${!isOutOfStock ? `
                <div class="purchase-section">
                    <div class="quantity-section">
                        <label class="quantity-label">Quantity:</label>
                        <div class="quantity-controls">
                            <button class="quantity-btn" onclick="app.decrementQuantity()">−</button>
                            <input type="number" id="quantityInput" class="quantity-input" 
                                   min="1" max="${product.stock}" value="1"
                                   oninput="app.validateQuantityInput()" onchange="app.validateQuantityInput()">
                            <button class="quantity-btn" onclick="app.incrementQuantity()">+</button>
                        </div>
                        <div id="quantityError" class="quantity-error hidden"></div>
                    </div>
                    
                    <div class="price-summary">
                        <div class="price-row">
                            <span>Unit Price:</span>
                            <span>$${this.formatPrice(product.price)}</span>
                        </div>
                        <div class="price-row">
                            <span>Quantity:</span>
                            <span id="summaryQuantity">1</span>
                        </div>
                        <div class="price-row price-total">
                            <span>Total:</span>
                            <span id="summaryTotal">$${this.formatPrice(product.price)}</span>
                        </div>
                    </div>
                    
                    <button class="add-to-cart-detail-btn" id="addToCartDetailBtn" onclick="app.addToCartFromDetail()">
                        🛒 Add to Cart
                    </button>
                </div>
            ` : `
                <div class="purchase-section">
                    <button class="add-to-cart-detail-btn" disabled>❌ Out of Stock</button>
                </div>
            `}
        `;
    }

    getStockStatus(stock) {
        if (stock === 0) {
            return { class: 'stock-out-of-stock', message: 'Out of Stock' };
        } else if (stock <= 5) {
            return { class: 'stock-low-stock', message: `Only ${stock} left in stock!` };
        } else {
            return { class: 'stock-in-stock', message: `${stock} in stock` };
        }
    }

    incrementQuantity() {
        const input = document.getElementById('quantityInput');
        if (input && parseInt(input.value) < this.currentProduct.stock) {
            input.value = parseInt(input.value) + 1;
            this.validateQuantityInput();
            this.updatePriceSummary();
        }
    }

    decrementQuantity() {
        const input = document.getElementById('quantityInput');
        if (input && parseInt(input.value) > 1) {
            input.value = parseInt(input.value) - 1;
            this.validateQuantityInput();
            this.updatePriceSummary();
        }
    }

    updatePriceSummary() {
        const quantityInput = document.getElementById('quantityInput');
        const summaryQuantity = document.getElementById('summaryQuantity');
        const summaryTotal = document.getElementById('summaryTotal');
        
        if (quantityInput && summaryQuantity && summaryTotal && this.currentProduct) {
            const quantity = parseInt(quantityInput.value) || 1;
            const total = this.currentProduct.price * quantity;
            
            summaryQuantity.textContent = quantity;
            summaryTotal.textContent = `$${this.formatPrice(total)}`;
        }
    }

    validateQuantityInput() {
        const quantityInput = document.getElementById('quantityInput');
        const quantityError = document.getElementById('quantityError');
        const addToCartBtn = document.getElementById('addToCartDetailBtn');
        
        if (!quantityInput || !this.currentProduct) return;

        const quantity = parseInt(quantityInput.value);
        const maxStock = this.currentProduct.stock;
        let isValid = true;

        if (isNaN(quantity) || quantity < 1) {
            quantityError.textContent = 'Please enter a valid quantity';
            isValid = false;
        } else if (quantity > maxStock) {
            quantityError.textContent = `Only ${maxStock} items available in stock`;
            isValid = false;
        } else {
            quantityError.textContent = '';
        }

        if (quantityError) {
            quantityError.classList.toggle('hidden', isValid);
        }
        if (addToCartBtn) {
            addToCartBtn.disabled = !isValid;
        }

        this.updatePriceSummary();
        return isValid;
    }

    async addToCartFromDetail() {
        if (!this.currentProduct || !this.validateQuantityInput()) return;

        const quantityInput = document.getElementById('quantityInput');
        const quantity = parseInt(quantityInput.value);

        try {
            for (let i = 0; i < quantity; i++) {
                await this.addToCart(this.currentProduct.id);
            }
            this.showNotification(`${quantity} item(s) added to cart!`, 'success');
        } catch (error) {
            this.showNotification('Failed to add items to cart', 'error');
        }
    }

    formatPrice(price) {
        return new Intl.NumberFormat('en-US', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(price);
    }

    hideAllSections() {
        this.productsSection.classList.add('hidden');
        this.cartSection.classList.add('hidden');
        if (this.productDetailSection) {
            this.productDetailSection.classList.add('hidden');
        }
    }

    showCart() {
        if (!this.cart || !this.cart.items || this.cart.items.length === 0) {
            this.cartItems.innerHTML = '<div class="empty-cart">Your cart is empty</div>';
        } else {
            this.renderCart();
        }
        
        this.hideAllSections();
        this.cartSection.classList.remove('hidden');
    }

    renderCart() {
        const total = this.cart.items.reduce((sum, item) => 
            sum + (item.product.price * item.quantity), 0);

        this.cartItems.innerHTML = `
            ${this.cart.items.map(item => `
                <div class="cart-item">
                    <div class="cart-item-info">
                        <div class="cart-item-name">${this.escapeHtml(item.product.name)}</div>
                        <div class="cart-item-price">$${item.product.price.toFixed(2)} each</div>
                    </div>
                    <div class="cart-item-quantity">Qty: ${item.quantity}</div>
                </div>
            `).join('')}
            <div class="cart-item" style="background: #ecf0f1; font-weight: bold;">
                <div class="cart-item-info">
                    <div class="cart-item-name">Total</div>
                </div>
                <div class="cart-item-price" style="font-size: 1.2rem;">$${total.toFixed(2)}</div>
            </div>
        `;
    }

    showProducts() {
        this.hideAllSections();
        this.productsSection.classList.remove('hidden');
    }

    async checkout() {
        if (!this.cartId) {
            this.showNotification('No cart to checkout', 'error');
            return;
        }

        try {
            const response = await fetch(`/api/carts/${this.cartId}/checkout`, {
                method: 'DELETE'
            });

            if (!response.ok) throw new Error('Checkout failed');

            this.showNotification('Checkout successful! Order placed.', 'success');
            
            // Reset cart
            this.cartId = null;
            this.cart = null;
            localStorage.removeItem('cartId');
            this.updateCartCount();
            this.showProducts();
        } catch (error) {
            this.showNotification('Checkout failed', 'error');
        }
    }

    updateCartCount() {
        const count = this.cart && this.cart.items ? 
            this.cart.items.reduce((sum, item) => sum + item.quantity, 0) : 0;
        this.cartCount.textContent = count;
    }

    showNotification(message, type) {
        this.notification.textContent = message;
        this.notification.className = `notification ${type}`;
        this.notification.classList.remove('hidden');

        setTimeout(() => {
            this.notification.classList.add('hidden');
        }, 3000);
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Initialize the app when the page loads
let app;
document.addEventListener('DOMContentLoaded', () => {
    app = new MarketplaceApp();
});
