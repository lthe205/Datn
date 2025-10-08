// Cart JavaScript functionality
class CartManager {
    constructor() {
        this.init();
        // Ensure any leftover overlay is removed on load
        this.forceHideLoading();
        // Allow ESC to close overlay if stuck
        window.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                this.forceHideLoading();
            }
        });
    }

    init() {
        this.loadCart();
        this.bindEvents();
    }

    bindEvents() {
        // Update quantity buttons
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('update-quantity')) {
                this.updateQuantity(e.target);
            }
            if (e.target.classList.contains('remove-item')) {
                this.removeItem(e.target);
            }
        });

        // Quantity input change
        document.addEventListener('change', (e) => {
            if (e.target.classList.contains('quantity-input')) {
                this.updateQuantityFromInput(e.target);
            }
        });
    }

    async loadCart() {
        try {
            this.showLoading();
            const response = await this.fetchWithTimeout('/api/cart', {
                credentials: 'include'
            });
            if (!response.ok) throw new Error('HTTP ' + response.status);
            const data = await response.json();

            if (data.success) {
                this.renderCart(data.cartItems, data.total, data.itemCount);
            } else {
                this.showError(data.message);
            }
        } catch (error) {
            console.error('Error loading cart:', error);
            this.showError('Có lỗi xảy ra khi tải giỏ hàng');
        } finally {
            this.hideLoading();
        }
    }

    renderCart(cartItems, total, itemCount) {
        const cartContent = document.getElementById('cart-content');
        const emptyCart = document.getElementById('empty-cart');
        const checkoutSection = document.getElementById('checkout-section');
        const cartCount = document.getElementById('cart-count');

        // Update cart count in navigation
        cartCount.textContent = itemCount;

        if (cartItems.length === 0) {
            cartContent.style.display = 'none';
            emptyCart.style.display = 'block';
            checkoutSection.style.display = 'none';
            return;
        }

        cartContent.style.display = 'block';
        emptyCart.style.display = 'none';
        checkoutSection.style.display = 'block';

        cartContent.innerHTML = `
            <div class="row">
                <div class="col-lg-8">
                    ${cartItems.map(item => this.renderCartItem(item)).join('')}
                </div>
                <div class="col-lg-4">
                    <div class="total-section">
                        <h4>Tổng kết đơn hàng</h4>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Tạm tính:</span>
                            <span class="price">${this.formatCurrency(total)}</span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between">
                            <strong>Tổng cộng:</strong>
                            <strong class="price">${this.formatCurrency(total)}</strong>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    renderCartItem(item) {
        return `
            <div class="cart-item">
                <div class="row align-items-center">
                    <div class="col-md-2">
                        <img src="${item.sanPham.anhChinh || '/images/no-image.jpg'}" 
                             alt="${item.sanPham.ten}" 
                             class="product-image">
                    </div>
                    <div class="col-md-4">
                        <h5>${item.sanPham.ten}</h5>
                        ${item.kichCo ? `<p class="text-muted mb-1">Kích cỡ: ${item.kichCo}</p>` : ''}
                        ${item.mauSac ? `<p class="text-muted mb-1">Màu sắc: ${item.mauSac}</p>` : ''}
                    </div>
                    <div class="col-md-2">
                        <div class="input-group">
                            <button class="btn btn-outline-secondary update-quantity" 
                                    data-item-id="${item.id}" 
                                    data-action="decrease">-</button>
                            <input type="number" 
                                   class="form-control quantity-input text-center" 
                                   id="qty-${item.id}"
                                   name="quantity-${item.id}"
                                   autocomplete="off"
                                   value="${item.soLuong}" 
                                   min="1" 
                                   data-item-id="${item.id}">
                            <button class="btn btn-outline-secondary update-quantity" 
                                    data-item-id="${item.id}" 
                                    data-action="increase">+</button>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <span class="price">${this.formatCurrency(item.gia)}</span>
                    </div>
                    <div class="col-md-2">
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="price">${this.formatCurrency((item.gia || 0) * (item.soLuong || 0))}</span>
                            <button class="btn btn-outline-danger btn-sm remove-item" 
                                    data-item-id="${item.id}">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    async updateQuantity(button) {
        const itemId = button.dataset.itemId;
        const action = button.dataset.action;
        const quantityInput = document.querySelector(`input[data-item-id="${itemId}"]`);
        let newQuantity = parseInt(quantityInput.value);

        if (action === 'increase') {
            newQuantity += 1;
        } else if (action === 'decrease') {
            newQuantity = Math.max(1, newQuantity - 1);
        }

        await this.updateQuantityRequest(itemId, newQuantity);
    }

    async updateQuantityFromInput(input) {
        const itemId = input.dataset.itemId;
        const newQuantity = parseInt(input.value);

        if (newQuantity < 1) {
            input.value = 1;
            return;
        }

        await this.updateQuantityRequest(itemId, newQuantity);
    }

    async updateQuantityRequest(itemId, quantity) {
        try {
            this.showLoading();
            const response = await this.fetchWithTimeout(`/api/cart/update/${itemId}?quantity=${quantity}`, {
                method: 'PUT',
                credentials: 'include'
            });
            if (!response.ok) throw new Error('HTTP ' + response.status);
            const data = await response.json();

            if (data.success) {
                this.showSuccess(data.message);
                this.loadCart(); // Reload cart to update totals
            } else {
                this.showError(data.message);
            }
        } catch (error) {
            console.error('Error updating quantity:', error);
            this.showError('Có lỗi xảy ra khi cập nhật số lượng');
        } finally {
            this.hideLoading();
        }
    }

    async removeItem(button) {
        const itemId = button.dataset.itemId;

        if (!confirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?')) {
            return;
        }

        try {
            this.showLoading();
            const response = await this.fetchWithTimeout(`/api/cart/remove/${itemId}`, {
                method: 'DELETE',
                credentials: 'include'
            });
            if (!response.ok) throw new Error('HTTP ' + response.status);
            const data = await response.json();

            if (data.success) {
                this.showSuccess(data.message);
                this.loadCart(); // Reload cart
            } else {
                this.showError(data.message);
            }
        } catch (error) {
            console.error('Error removing item:', error);
            this.showError('Có lỗi xảy ra khi xóa sản phẩm');
        } finally {
            this.hideLoading();
        }
    }

    async clearCart() {
        if (!confirm('Bạn có chắc chắn muốn xóa toàn bộ giỏ hàng?')) {
            return;
        }

        try {
            this.showLoading();
            const response = await this.fetchWithTimeout('/api/cart/clear', {
                method: 'DELETE',
                credentials: 'include'
            });
            if (!response.ok) throw new Error('HTTP ' + response.status);
            const data = await response.json();

            if (data.success) {
                this.showSuccess(data.message);
                this.loadCart(); // Reload cart
            } else {
                this.showError(data.message);
            }
        } catch (error) {
            console.error('Error clearing cart:', error);
            this.showError('Có lỗi xảy ra khi xóa giỏ hàng');
        } finally {
            this.hideLoading();
        }
    }

    formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    }

    showLoading() {
        const overlay = document.getElementById('loadingOverlay');
        if (!overlay) return;
        overlay.classList.remove('d-none');
        clearTimeout(this.loadingTimer);
        this.loadingTimer = setTimeout(() => this.forceHideLoading(), 10000);
    }

    hideLoading() {
        const overlay = document.getElementById('loadingOverlay');
        if (!overlay) return;
        overlay.classList.add('d-none');
        clearTimeout(this.loadingTimer);
    }

    async fetchWithTimeout(url, options = {}, timeoutMs = 15000) {
        const controller = new AbortController();
        const id = setTimeout(() => controller.abort(), timeoutMs);
        try {
            const resp = await fetch(url, { ...options, signal: controller.signal });
            return resp;
        } finally {
            clearTimeout(id);
        }
    }

    forceHideLoading() {
        const overlay = document.getElementById('loadingOverlay');
        if (overlay) overlay.classList.add('d-none');
    }

    showSuccess(message) {
        const toast = document.getElementById('successToast');
        const messageElement = document.getElementById('successMessage');
        messageElement.textContent = message;
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
    }

    showError(message) {
        const toast = document.getElementById('errorToast');
        const messageElement = document.getElementById('errorMessage');
        messageElement.textContent = message;
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
    }
}

// Initialize cart manager when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new CartManager();
});
