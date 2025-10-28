class MarketplaceApp {
    constructor() {
        this.products = [];
        this.cart = null;
        this.currentProduct = null;
        
        this.initializeElements();
        this.attachEventListeners();
        this.loadProducts();
        this.loadCart();
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
            this.productsList.innerHTML = '<div class="loading">製品を読み込んでいます...</div>';
            
            const response = await fetch('/api/products');
            if (!response.ok) throw new Error('Failed to load products');
            
            this.products = await response.json();
            this.renderProducts();
        } catch (error) {
            this.showNotification('製品の読み込みに失敗しました', 'error');
            this.productsList.innerHTML = '<div class="loading">製品の読み込みに失敗しました</div>';
        }
    }

    renderProducts() {
        if (this.products.length === 0) {
            this.productsList.innerHTML = '<div class="loading">利用可能な製品がありません</div>';
            return;
        }

        this.productsList.innerHTML = this.products.map(product => `
            <div class="product-card">
                <div class="product-name">${this.escapeHtml(product.name)}</div>
                <div class="product-description">${this.escapeHtml(product.description)}</div>
                <div class="product-price">$${product.price.toFixed(2)}</div>
                <button class="add-to-cart-btn list-btn-padding" data-product-id="${product.id}" onclick="app.addToCartWithButton('${product.id}', event)">
                    カートに追加
                </button>
                <button class="view-details-btn list-btn-padding" onclick="app.showProductDetail('${product.id}')">
                    詳細を見る
                </button>
            </div>
        `).join('');
    }

    async addToCartWithButton(productId, event) {
        await this.addToCart(productId, 1);
    }

    async loadCart() {
        try {
            const response = await fetch('/api/carts/session');
            if (!response.ok) throw new Error('Failed to load cart');

            this.cart = await response.json();
            this.updateCartCount();
        } catch (error) {
            console.error('Error loading cart:', error);
            this.showNotification('カートの読み込みに失敗しました', 'error');
        }
    }

    async checkout() {
        if (!this.cart || !this.cart.id) {
            this.showNotification('チェックアウトするカートがありません', 'error');
            return;
        }

        try {
            const response = await fetch(`/api/carts/${this.cart.id}/checkout`, {
                method: 'DELETE'
            });

            if (!response.ok) throw new Error('Checkout failed');

            this.showNotification('チェックアウトが完了しました。ご注文ありがとうございます。', 'success');
            
            this.cart = null;
            this.updateCartCount();
            await this.loadCart();
            this.showProducts();
        } catch (error) {
            this.showNotification('チェックアウトに失敗しました', 'error');
        }
    }

    async addToCart(productId, quantity = 1) {
        try {
            const response = await fetch('/api/carts/session/add-product', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ 
                    productId: productId,
                    quantity: quantity 
                })
            });

            if (!response.ok) {
                const errorData = await response.json();
                if (errorData.message && errorData.message.includes('Insufficient stock')) {
                    this.showNotification(errorData.message, 'error', 5000);
                } else {
                    throw new Error(errorData.message || 'Failed to add product to cart');
                }
                return;
            }

            this.cart = await response.json();
            this.updateCartCount();
            this.showNotification(`${quantity}個の商品をカートに追加しました！`, 'success');
            
            if (this.currentProduct && this.currentProduct.id === productId) {
                this.refreshProductStock(productId);
            }
        } catch (error) {
            console.error('Error adding to cart:', error);
            this.showNotification(error.message || '商品をカートに追加できませんでした', 'error');
        }
    }

    async refreshProductStock(productId) {
        try {
            const response = await fetch(`/api/products/${productId}`);
            if (response.ok) {
                const updatedProduct = await response.json();
                this.currentProduct = updatedProduct;
                this.renderProductDetail();
            }
        } catch (error) {
            console.error('Failed to refresh product stock:', error);
        }
    }

    async loadCart() {
        try {
            const response = await fetch('/api/carts/session');
            if (!response.ok) throw new Error('Failed to load cart');

            this.cart = await response.json();
            this.updateCartCount();
        } catch (error) {
            console.error('Error loading cart:', error);
            this.showNotification('カートの読み込みに失敗しました', 'error');
        }
    }

    async showProductDetail(productId) {
        try {
            this.productDetail.innerHTML = '<div class="loading">製品詳細を読み込んでいます...</div>';
            this.hideAllSections();
            this.productDetailSection.classList.remove('hidden');

            const response = await fetch(`/api/products/${productId}`);
            if (!response.ok) {
                if (response.status === 404) {
                    this.productDetail.innerHTML = `
                        <div class="error-page">
                            <div class="error-icon">🔍</div>
                            <h3>商品が見つかりません。</h3>
                            <p>お探しの商品は存在しないか、削除された可能性があります。</p>
                            <button class="retry-btn" onclick="app.showProducts()">製品一覧に戻る</button>
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
            this.showNotification('製品詳細の読み込みに失敗しました', 'error');
            this.productDetail.innerHTML = `
                <div class="error-page">
                    <h3>製品の読み込みエラー</h3>
                    <p>製品詳細を読み込めませんでした。もう一度お試しください。</p>
                    <button class="retry-btn" onclick="app.showProducts()">製品一覧に戻る</button>
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
                        <label class="quantity-label">数量:</label>
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
                            <span>単価:</span>
                            <span>$${this.formatPrice(product.price)}</span>
                        </div>
                        <div class="price-row">
                            <span>数量:</span>
                            <span id="summaryQuantity">1</span>
                        </div>
                        <div class="price-row price-total">
                            <span>合計:</span>
                            <span id="summaryTotal">$${this.formatPrice(product.price)}</span>
                        </div>
                    </div>
                    
                    <button class="add-to-cart-btn details-btn-padding" id="addToCartDetailBtn" onclick="app.addToCartFromDetail()">
                        🛒 カートに追加
                    </button>
                </div>
            ` : `
                <div class="purchase-section">
                    <button class="add-to-cart-btn" disabled>❌ 在庫切れ</button>
                </div>
            `}
        `;
    }

    getStockStatus(stock) {
        if (stock === 0) {
            return { class: 'stock-out-of-stock', message: '在庫切れ' };
        } else if (stock <= 5) {
            return { class: 'stock-low-stock', message: `在庫残りわずか ${stock} 点` };
        } else {
            return { class: 'stock-in-stock', message: `在庫あり ${stock} 点` };
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
        let errorMessage = '';

        let existingQuantity = 0;
        if (this.cart && this.cart.items) {
            const existingItem = this.cart.items.find(item => item.product.id === this.currentProduct.id);
            if (existingItem) {
                existingQuantity = existingItem.quantity;
            }
        }

        const availableStock = maxStock - existingQuantity;

        if (isNaN(quantity) || quantity < 1) {
            errorMessage = '有効な数量を入力してください';
            isValid = false;
        } else if (quantity > availableStock) {
            if (existingQuantity > 0) {
                errorMessage = `追加できるのはあと ${availableStock} 点のみです (カートに ${existingQuantity} 点あり)`;
            } else {
                errorMessage = `在庫は ${availableStock} 点のみです`;
            }
            isValid = false;
        }

        if (quantityError) {
            quantityError.textContent = errorMessage;
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
        await this.addToCart(this.currentProduct.id, quantity);
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
            this.cartItems.innerHTML = '<div class="empty-cart">カートは空です</div>';
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
                    <div class="cart-item-quantity">数量: ${item.quantity}</div>
                </div>
            `).join('')}
            <div class="cart-item" style="background: #ecf0f1; font-weight: bold;">
                <div class="cart-item-info">
                    <div class="cart-item-name">合計</div>
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
        if (!this.cart || !this.cart.id) {
            this.showNotification('チェックアウトするカートがありません', 'error');
            return;
        }

        try {
            const response = await fetch(`/api/carts/${this.cart.id}/checkout`, {
                method: 'DELETE'
            });

            if (!response.ok) throw new Error('Checkout failed');

            this.showNotification('チェックアウトが完了しました。ご注文ありがとうございます。', 'success');
            
            this.cart = null;
            this.updateCartCount();
            await this.loadCart();
            this.showProducts();
        } catch (error) {
            this.showNotification('チェックアウトに失敗しました', 'error');
        }
    }

    updateCartCount() {
        const count = this.cart && this.cart.items ? 
            this.cart.items.reduce((sum, item) => sum + item.quantity, 0) : 0;
        this.cartCount.textContent = count;
    }

    showNotification(message, type, duration = 3000) {
        this.notification.textContent = message;
        this.notification.className = `notification ${type}`;
        this.notification.classList.remove('hidden');

        clearTimeout(this.notificationTimeout);
        this.notificationTimeout = setTimeout(() => {
            this.notification.classList.add('hidden');
        }, duration);
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

let app;
document.addEventListener('DOMContentLoaded', () => {
    app = new MarketplaceApp();
});
