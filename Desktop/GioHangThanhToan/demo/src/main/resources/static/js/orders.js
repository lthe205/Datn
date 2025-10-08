// Orders JavaScript functionality
class OrdersManager {
    constructor() {
        this.currentFilter = 'all';
        this.init();
    }

    init() {
        this.loadOrderStats();
        this.loadOrders();
        this.bindEvents();
    }

    bindEvents() {
        // Filter tabs
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('filter-tab')) {
                e.preventDefault();
                this.filterOrders(e.target.dataset.status);
            }
        });

        // Order actions
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('view-details')) {
                this.viewOrderDetails(e.target.dataset.orderId);
            }
            if (e.target.classList.contains('cancel-order')) {
                this.showCancelModal(e.target.dataset.orderId);
            }
            if (e.target.classList.contains('track-order')) {
                this.trackOrder(e.target.dataset.orderId);
            }
        });

        // Cancel order confirmation
        document.getElementById('confirm-cancel-btn').addEventListener('click', () => {
            this.cancelOrder();
        });
    }

    async loadOrderStats() {
        try {
            const response = await fetch('/api/orders/stats', {
                credentials: 'include'
            });
            const data = await response.json();

            if (data.success) {
                this.renderOrderStats(data.stats);
            } else {
                this.showError(data.message);
            }
        } catch (error) {
            console.error('Error loading order stats:', error);
            this.showError('Có lỗi xảy ra khi tải thống kê đơn hàng');
        }
    }

    renderOrderStats(stats) {
        document.getElementById('total-orders').textContent = stats.totalOrders;
        document.getElementById('pending-orders').textContent = stats.pendingOrders;
        document.getElementById('completed-orders').textContent = stats.completedOrders;
        document.getElementById('total-spent').textContent = this.formatCurrency(stats.totalSpent);
    }

    async loadOrders() {
        try {
            const response = await fetch('/api/orders', {
                credentials: 'include'
            });
            const data = await response.json();

            if (data.success) {
                this.allOrders = data.orders;
                this.filterOrders(this.currentFilter);
            } else {
                this.showError(data.message);
            }
        } catch (error) {
            console.error('Error loading orders:', error);
            this.showError('Có lỗi xảy ra khi tải danh sách đơn hàng');
        }
    }

    filterOrders(status) {
        this.currentFilter = status;
        
        // Update filter tabs
        document.querySelectorAll('.filter-tab').forEach(tab => {
            tab.classList.remove('active');
        });
        document.querySelector(`[data-status="${status}"]`).classList.add('active');

        // Filter orders
        let filteredOrders = this.allOrders;
        if (status !== 'all') {
            filteredOrders = this.allOrders.filter(order => order.trangThai === status);
        }

        this.renderOrders(filteredOrders);
    }

    renderOrders(orders) {
        const ordersList = document.getElementById('orders-list');
        const emptyOrders = document.getElementById('empty-orders');

        if (orders.length === 0) {
            ordersList.style.display = 'none';
            emptyOrders.style.display = 'block';
            return;
        }

        ordersList.style.display = 'block';
        emptyOrders.style.display = 'none';

        ordersList.innerHTML = orders.map(order => this.renderOrderCard(order)).join('');
    }

    renderOrderCard(order) {
        const statusClass = this.getStatusClass(order.trangThai);
        const statusText = this.getStatusText(order.trangThai);
        const canCancel = order.trangThai === 'CHO_XAC_NHAN';

        return `
            <div class="order-card">
                <div class="order-header">
                    <div class="row align-items-center">
                        <div class="col-md-6">
                            <h5 class="mb-1">Đơn hàng #${order.maDonHang}</h5>
                            <small class="text-muted">${this.formatDate(order.ngayTao)}</small>
                        </div>
                        <div class="col-md-6 text-end">
                            <span class="status-badge ${statusClass}">${statusText}</span>
                        </div>
                    </div>
                </div>
                <div class="order-body">
                    <div class="row">
                        <div class="col-md-8">
                            <div class="order-items-preview">
                                ${order.orderItems.slice(0, 3).map(item => `
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
                                                <div class="fw-bold">${this.formatCurrency(item.thanhTien || (item.gia * item.soLuong))}</div>
                                            </div>
                                        </div>
                                    </div>
                                `).join('')}
                                ${order.orderItems.length > 3 ? `
                                    <div class="text-center mt-2">
                                        <small class="text-muted">và ${order.orderItems.length - 3} sản phẩm khác</small>
                                    </div>
                                ` : ''}
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="order-summary">
                                <div class="d-flex justify-content-between mb-2">
                                    <span>Tạm tính:</span>
                                    <span>${this.formatCurrency(order.tongTien)}</span>
                                </div>
                                <div class="d-flex justify-content-between mb-2">
                                    <span>Phí vận chuyển:</span>
                                    <span>${this.formatCurrency(order.phiVanChuyen)}</span>
                                </div>
                                <hr>
                                <div class="d-flex justify-content-between mb-3">
                                    <strong>Tổng cộng:</strong>
                                    <strong class="text-danger">${this.formatCurrency(order.tongThanhToan)}</strong>
                                </div>
                                <div class="d-grid gap-2">
                                    <button class="btn btn-outline-primary btn-sm view-details" 
                                            data-order-id="${order.id}">
                                        <i class="fas fa-eye me-1"></i>Xem chi tiết
                                    </button>
                                    ${canCancel ? `
                                        <button class="btn btn-outline-danger btn-sm cancel-order" 
                                                data-order-id="${order.id}">
                                            <i class="fas fa-times me-1"></i>Hủy đơn hàng
                                        </button>
                                    ` : ''}
                                    ${order.trangThai === 'DANG_GIAO' ? `
                                        <button class="btn btn-outline-success btn-sm track-order" 
                                                data-order-id="${order.id}">
                                            <i class="fas fa-truck me-1"></i>Theo dõi đơn hàng
                                        </button>
                                    ` : ''}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    async viewOrderDetails(orderId) {
        try {
            const response = await fetch(`/api/orders/${orderId}`, {
                credentials: 'include'
            });
            const data = await response.json();

            if (data.success) {
                this.showOrderDetailsModal(data.order);
            } else {
                this.showError(data.message);
            }
        } catch (error) {
            console.error('Error loading order details:', error);
            this.showError('Có lỗi xảy ra khi tải chi tiết đơn hàng');
        }
    }

    showOrderDetailsModal(order) {
        const modalContent = document.getElementById('order-details-content');
        
        modalContent.innerHTML = `
            <div class="row">
                <div class="col-md-6">
                    <h5>Thông tin đơn hàng</h5>
                    <table class="table table-sm">
                        <tr>
                            <td><strong>Mã đơn hàng:</strong></td>
                            <td>${order.maDonHang}</td>
                        </tr>
                        <tr>
                            <td><strong>Ngày đặt:</strong></td>
                            <td>${this.formatDate(order.ngayTao)}</td>
                        </tr>
                        <tr>
                            <td><strong>Trạng thái:</strong></td>
                            <td><span class="status-badge ${this.getStatusClass(order.trangThai)}">${this.getStatusText(order.trangThai)}</span></td>
                        </tr>
                        <tr>
                            <td><strong>Phương thức thanh toán:</strong></td>
                            <td>${order.phuongThucThanhToan}</td>
                        </tr>
                        <tr>
                            <td><strong>Đã thanh toán:</strong></td>
                            <td>${order.daThanhToan ? 'Có' : 'Chưa'}</td>
                        </tr>
                    </table>
                </div>
                <div class="col-md-6">
                    <h5>Thông tin giao hàng</h5>
                    <table class="table table-sm">
                        <tr>
                            <td><strong>Người nhận:</strong></td>
                            <td>${order.tenNguoiNhan}</td>
                        </tr>
                        <tr>
                            <td><strong>Số điện thoại:</strong></td>
                            <td>${order.soDienThoaiNhan}</td>
                        </tr>
                        <tr>
                            <td><strong>Địa chỉ:</strong></td>
                            <td>${order.diaChiGiaoHang}</td>
                        </tr>
                    </table>
                </div>
            </div>
            
            <h5 class="mt-4">Chi tiết sản phẩm</h5>
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Sản phẩm</th>
                            <th>Kích cỡ</th>
                            <th>Màu sắc</th>
                            <th>Số lượng</th>
                            <th>Giá</th>
                            <th>Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${order.orderItems.map(item => `
                            <tr>
                                <td>
                                    <div class="d-flex align-items-center">
                                        <img src="${item.sanPham.anhChinh || '/images/no-image.jpg'}" 
                                             alt="${item.sanPham.ten}" 
                                             class="product-image me-2">
                                        <span>${item.sanPham.ten}</span>
                                    </div>
                                </td>
                                <td>${item.kichCo || '-'}</td>
                                <td>${item.mauSac || '-'}</td>
                                <td>${item.soLuong}</td>
                                <td>${this.formatCurrency(item.gia)}</td>
                                <td>${this.formatCurrency(item.thanhTien || (item.gia * item.soLuong))}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
            
            <div class="row mt-4">
                <div class="col-md-6 offset-md-6">
                    <div class="total-breakdown">
                        <div class="d-flex justify-content-between mb-2">
                            <span>Tạm tính:</span>
                            <span>${this.formatCurrency(order.tongTien)}</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span>Phí vận chuyển:</span>
                            <span>${this.formatCurrency(order.phiVanChuyen)}</span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between">
                            <strong>Tổng cộng:</strong>
                            <strong class="text-danger">${this.formatCurrency(order.tongThanhToan)}</strong>
                        </div>
                    </div>
                </div>
            </div>
        `;

        const modal = new bootstrap.Modal(document.getElementById('orderDetailsModal'));
        modal.show();
    }

    showCancelModal(orderId) {
        this.cancelOrderId = orderId;
        const modal = new bootstrap.Modal(document.getElementById('cancelOrderModal'));
        modal.show();
    }

    async cancelOrder() {
        if (!this.cancelOrderId) return;

        const reason = document.getElementById('cancel-reason').value;

        try {
            this.showLoading();
            
            const response = await fetch(`/api/orders/${this.cancelOrderId}/cancel`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                credentials: 'include',
                body: new URLSearchParams({
                    reason: reason
                })
            });

            const data = await response.json();

            if (data.success) {
                this.showSuccess('Đơn hàng đã được hủy thành công');
                
                // Close modal
                const modal = bootstrap.Modal.getInstance(document.getElementById('cancelOrderModal'));
                modal.hide();
                
                // Reload orders
                this.loadOrders();
                this.loadOrderStats();
            } else {
                this.showError(data.message);
            }
        } catch (error) {
            console.error('Error cancelling order:', error);
            this.showError('Có lỗi xảy ra khi hủy đơn hàng');
        } finally {
            this.hideLoading();
        }
    }

    trackOrder(orderId) {
        // This would typically redirect to a tracking page or show tracking info
        this.showSuccess('Tính năng theo dõi đơn hàng sẽ được cập nhật sớm');
    }

    getStatusClass(status) {
        switch (status) {
            case 'CHO_XAC_NHAN': return 'status-pending';
            case 'DANG_CHUAN_BI': return 'status-confirmed';
            case 'DANG_GIAO': return 'status-shipping';
            case 'DA_GIAO': return 'status-delivered';
            case 'DA_HUY': return 'status-cancelled';
            default: return 'status-pending';
        }
    }

    getStatusText(status) {
        switch (status) {
            case 'CHO_XAC_NHAN': return 'Chờ xác nhận';
            case 'DANG_CHUAN_BI': return 'Đang chuẩn bị';
            case 'DANG_GIAO': return 'Đang giao';
            case 'DA_GIAO': return 'Đã giao';
            case 'DA_HUY': return 'Đã hủy';
            default: return status;
        }
    }

    formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    }

    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    showLoading() {
        const modal = new bootstrap.Modal(document.getElementById('loadingModal'));
        modal.show();
    }

    hideLoading() {
        const modal = bootstrap.Modal.getInstance(document.getElementById('loadingModal'));
        if (modal) {
            modal.hide();
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
}

// Initialize orders manager when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new OrdersManager();
});
