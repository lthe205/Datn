// Checkout JavaScript functionality
class CheckoutManager {
    constructor() {
        this.selectedAddressId = null;
        this.selectedPaymentMethod = 'COD';
        this.init();
    }

    init() {
        this.loadCartItems();
        this.loadAddresses();
        this.loadProvinces();
        this.bindEvents();
    }

    bindEvents() {
        // Address selection
        document.addEventListener('click', (e) => {
            if (e.target.closest('.address-card')) {
                this.selectAddress(e.target.closest('.address-card'));
            }
        });

        // Payment method selection
        document.addEventListener('click', (e) => {
            if (e.target.closest('.payment-method')) {
                this.selectPaymentMethod(e.target.closest('.payment-method'));
            }
        });

        // Save address
        document.getElementById('save-address-btn').addEventListener('click', () => {
            this.saveAddress();
        });

        // Place order
        document.getElementById('place-order-btn').addEventListener('click', () => {
            this.placeOrder();
        });

        // Province change
        document.getElementById('province').addEventListener('change', (e) => {
            this.onProvinceChange(e.target.value);
        });

        // District change
        document.getElementById('district').addEventListener('change', (e) => {
            this.onDistrictChange(e.target.value);
        });
    }

    async loadCartItems() {
        try {
            const response = await this.fetchWithTimeout('/api/cart');
            if (!response.ok) throw new Error('HTTP ' + response.status);
            const data = await response.json();

            if (data.success) {
                this.renderOrderItems(data.cartItems);
                this.calculateTotals(data.total);
            } else {
                this.showError(data.message);
            }
        } catch (error) {
            console.error('Error loading cart items:', error);
            this.showError('Có lỗi xảy ra khi tải giỏ hàng');
        }
    }

    renderOrderItems(cartItems) {
        const orderItemsContainer = document.getElementById('order-items');
        
        if (cartItems.length === 0) {
            orderItemsContainer.innerHTML = '<p class="text-muted">Giỏ hàng trống</p>';
            return;
        }

        orderItemsContainer.innerHTML = cartItems.map(item => `
            <div class="order-item">
                <div class="d-flex align-items-center">
                    <img src="${item.sanPham.anhChinh || '/images/no-image.jpg'}" 
                         alt="${item.sanPham.ten}" 
                         class="product-image me-3">
                    <div class="flex-grow-1">
                        <h6 class="mb-1">${item.sanPham.ten}</h6>
                        ${item.kichCo ? `<small class="text-muted">Kích cỡ: ${item.kichCo}</small><br>` : ''}
                        ${item.mauSac ? `<small class="text-muted">Màu sắc: ${item.mauSac}</small><br>` : ''}
                        <small class="text-muted">Số lượng: ${item.soLuong}</small>
                    </div>
                    <div class="text-end">
                        <div class="fw-bold">${this.formatCurrency((item.gia || 0) * (item.soLuong || 0))}</div>
                        <small class="text-muted">${this.formatCurrency(item.gia)} × ${item.soLuong}</small>
                    </div>
                </div>
            </div>
        `).join('');
    }

    calculateTotals(subtotal) {
        const subtotalElement = document.getElementById('subtotal');
        const shippingFeeElement = document.getElementById('shipping-fee');
        const totalAmountElement = document.getElementById('total-amount');

        subtotalElement.textContent = this.formatCurrency(subtotal);

        // Calculate shipping fee (simplified logic)
        let shippingFee = 0;
        if (subtotal < 500000) {
            shippingFee = 30000; // Default shipping fee
        }

        shippingFeeElement.textContent = this.formatCurrency(shippingFee);
        totalAmountElement.textContent = this.formatCurrency(subtotal + shippingFee);
    }

    async loadAddresses() {
        try {
            const response = await this.fetchWithTimeout('/api/addresses', {
                credentials: 'include'
            });
            if (!response.ok) throw new Error('HTTP ' + response.status);
            const data = await response.json();

            if (data.success) {
                this.renderAddresses(data.addresses);
            } else {
                this.showError(data.message);
            }
        } catch (error) {
            console.error('Error loading addresses:', error);
            this.showError('Có lỗi xảy ra khi tải địa chỉ');
        }
    }

    renderAddresses(addresses) {
        const addressList = document.getElementById('address-list');
        
        if (addresses.length === 0) {
            addressList.innerHTML = '<p class="text-muted">Chưa có địa chỉ nào</p>';
            return;
        }

        addressList.innerHTML = addresses.map(address => `
            <div class="address-card ${address.macDinh ? 'selected' : ''}" 
                 data-address-id="${address.id}">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <h6 class="mb-1">${address.hoTenNhan}</h6>
                        <p class="mb-1">${address.soDienThoai}</p>
                        <p class="mb-0 text-muted">${address.diaChi}, ${address.quanHuyen}, ${address.tinhThanh}</p>
                    </div>
                    ${address.macDinh ? '<span class="badge bg-primary">Mặc định</span>' : ''}
                </div>
            </div>
        `).join('');

        // Select default address
        if (addresses.length > 0) {
            const defaultAddress = addresses.find(addr => addr.macDinh) || addresses[0];
            this.selectedAddressId = defaultAddress.id;
        }
    }

    selectAddress(addressCard) {
        // Remove previous selection
        document.querySelectorAll('.address-card').forEach(card => {
            card.classList.remove('selected');
        });

        // Select current address
        addressCard.classList.add('selected');
        this.selectedAddressId = addressCard.dataset.addressId;
    }

    selectPaymentMethod(paymentCard) {
        // Remove previous selection
        document.querySelectorAll('.payment-method').forEach(card => {
            card.classList.remove('selected');
        });

        // Select current payment method
        paymentCard.classList.add('selected');
        this.selectedPaymentMethod = paymentCard.dataset.method;
    }

    async saveAddress() {
        const form = document.getElementById('address-form');

        // Get selected values
        const provinceSelect = document.getElementById('province');
        const districtSelect = document.getElementById('district');
        const wardSelect = document.getElementById('ward');

        const addressData = {
            hoTenNhan: document.getElementById('recipient-name').value,
            soDienThoai: document.getElementById('recipient-phone').value,
            diaChi: document.getElementById('address-detail').value,
            quanHuyen: districtSelect.options[districtSelect.selectedIndex]?.textContent || '',
            tinhThanh: provinceSelect.options[provinceSelect.selectedIndex]?.textContent || '',
            phuongXa: wardSelect.options[wardSelect.selectedIndex]?.textContent || '',
            macDinh: document.getElementById('set-default').checked
        };

        // Validate form
        if (!addressData.hoTenNhan || !addressData.soDienThoai || !addressData.diaChi || 
            !addressData.quanHuyen || !addressData.tinhThanh || !addressData.phuongXa) {
            this.showError('Vui lòng điền đầy đủ thông tin địa chỉ');
            return;
        }

        try {
            const response = await this.fetchWithTimeout('/api/addresses', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                credentials: 'include',
                body: new URLSearchParams(addressData)
            });
            
            if (!response.ok) throw new Error('HTTP ' + response.status);
            const data = await response.json();

            if (data.success) {
                const modal = bootstrap.Modal.getInstance(document.getElementById('addressModal'));
                modal.hide();
                
                this.showSuccess('Địa chỉ đã được lưu thành công');
                
                // Reload addresses
                this.loadAddresses();
                
                // Clear form
                form.reset();
            } else {
                this.showError(data.message);
            }
        } catch (error) {
            console.error('Error saving address:', error);
            this.showError('Có lỗi xảy ra khi lưu địa chỉ');
        }
    }

    async placeOrder() {
        const notes = document.getElementById('order-notes').value;

        // Kiểm tra xem có địa chỉ nào được chọn không
        if (!this.selectedAddressId) {
            this.showError('Vui lòng chọn địa chỉ giao hàng');
            return;
        }

        try {
            this.showLoading();

            const response = await this.fetchWithTimeout('/api/orders/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                credentials: 'include',
                body: new URLSearchParams({
                    addressId: this.selectedAddressId,
                    paymentMethod: this.selectedPaymentMethod,
                    notes: notes
                })
            });
            if (!response.ok) throw new Error('HTTP ' + response.status);
            const data = await response.json();

            if (data.success) {
                // Nếu là thanh toán VNPay, chuyển hướng đến trang thanh toán
                if (this.selectedPaymentMethod === 'VNPAY') {
                    await this.processVNPayPayment(data.order.id);
                } else {
                    this.showSuccess('Đơn hàng đã được tạo thành công!');
                    
                    // Redirect to order details or orders page
                    setTimeout(() => {
                        window.location.href = `/orders/${data.order.id}`;
                    }, 2000);
                }
            } else {
                this.showError(data.message);
            }
        } catch (error) {
            console.error('Error placing order:', error);
            this.showError('Có lỗi xảy ra khi đặt hàng');
        } finally {
            this.hideLoading();
        }
    }

    async processVNPayPayment(orderId) {
        try {
            // Lấy IP address của user
            const ipResponse = await fetch('https://api.ipify.org?format=json');
            const ipData = await ipResponse.json();
            const ipAddress = ipData.ip;

            // Tạo URL thanh toán VNPay
            const response = await this.fetchWithTimeout('/payment/vnpay/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                credentials: 'include',
                body: new URLSearchParams({
                    orderId: orderId,
                    ipAddress: ipAddress
                })
            });

            if (!response.ok) throw new Error('HTTP ' + response.status);
            const data = await response.json();

            if (data.success) {
                // Chuyển hướng đến trang thanh toán VNPay
                window.location.href = data.paymentUrl;
            } else {
                this.showError(data.message);
            }
        } catch (error) {
            console.error('Error creating VNPay payment:', error);
            this.showError('Có lỗi xảy ra khi tạo thanh toán VNPay');
        }
    }

    formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    }

    showLoading() {
        const modalEl = document.getElementById('loadingModal');
        if (!modalEl) return;
        if (!bootstrap.Modal.getInstance(modalEl)) new bootstrap.Modal(modalEl, {backdrop:'static', keyboard:false});
        const modal = bootstrap.Modal.getInstance(modalEl);
        modal.show();
    }

    hideLoading() {
        const modalEl = document.getElementById('loadingModal');
        if (!modalEl) return;
        const modal = bootstrap.Modal.getInstance(modalEl);
        if (modal) modal.hide();
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

    // Address API methods
    async loadProvinces() {
        try {
            const response = await this.fetchWithTimeout('/api/address/provinces', {
                credentials: 'include'
            });
            if (!response.ok) throw new Error('HTTP ' + response.status);
            const data = await response.json();

            if (data.success) {
                this.populateProvinceSelect(data.data);
            } else {
                console.error('Error loading provinces:', data.message);
            }
        } catch (error) {
            console.error('Error loading provinces:', error);
        }
    }

    populateProvinceSelect(provinces) {
        const select = document.getElementById('province');
        select.innerHTML = '<option value="">Chọn tỉnh/thành phố</option>';
        
        provinces.forEach(province => {
            const option = document.createElement('option');
            option.value = province.code;
            option.textContent = province.name;
            select.appendChild(option);
        });
    }

    async onProvinceChange(provinceCode) {
        const districtSelect = document.getElementById('district');
        const wardSelect = document.getElementById('ward');
        
        // Reset district and ward
        districtSelect.innerHTML = '<option value="">Chọn quận/huyện</option>';
        wardSelect.innerHTML = '<option value="">Chọn phường/xã</option>';
        districtSelect.disabled = true;
        wardSelect.disabled = true;

        if (!provinceCode) return;

        try {
            const response = await this.fetchWithTimeout(`/api/address/districts/${provinceCode}`, {
                credentials: 'include'
            });
            if (!response.ok) throw new Error('HTTP ' + response.status);
            const data = await response.json();

            if (data.success) {
                this.populateDistrictSelect(data.data);
                districtSelect.disabled = false;
            } else {
                console.error('Error loading districts:', data.message);
            }
        } catch (error) {
            console.error('Error loading districts:', error);
        }
    }

    populateDistrictSelect(districts) {
        const select = document.getElementById('district');
        select.innerHTML = '<option value="">Chọn quận/huyện</option>';
        
        districts.forEach(district => {
            const option = document.createElement('option');
            option.value = district.code;
            option.textContent = district.name;
            select.appendChild(option);
        });
    }

    async onDistrictChange(districtCode) {
        const wardSelect = document.getElementById('ward');
        
        // Reset ward
        wardSelect.innerHTML = '<option value="">Chọn phường/xã</option>';
        wardSelect.disabled = true;

        if (!districtCode) return;

        try {
            const response = await this.fetchWithTimeout(`/api/address/wards/${districtCode}`, {
                credentials: 'include'
            });
            if (!response.ok) throw new Error('HTTP ' + response.status);
            const data = await response.json();

            if (data.success) {
                this.populateWardSelect(data.data);
                wardSelect.disabled = false;
            } else {
                console.error('Error loading wards:', data.message);
            }
        } catch (error) {
            console.error('Error loading wards:', error);
        }
    }

    populateWardSelect(wards) {
        const select = document.getElementById('ward');
        select.innerHTML = '<option value="">Chọn phường/xã</option>';
        
        wards.forEach(ward => {
            const option = document.createElement('option');
            option.value = ward.code;
            option.textContent = ward.name;
            select.appendChild(option);
        });
    }
}

// Initialize checkout manager when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new CheckoutManager();
});
