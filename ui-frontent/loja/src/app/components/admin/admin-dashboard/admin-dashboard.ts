import { AfterViewChecked, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Chart, registerables } from 'chart.js';

import { AdminDashboardService } from '../../../services/admin/admin-dashboard.service';
import { AdminDashboardResponse } from '../../../common/admin/admin-dashboard-response';
import { AdminOrdersPerDayPoint } from '../../../common/admin/admin-orders-per-day-point';

;
import { AdminRevenuePerDayPoint } from '../../../common/admin/admin-revenue-per-day-point';
import { AdminTopProductPoint } from '../../../common/admin/admin-top-product-point';
import { AdminProductStockPoint } from '../../../common/admin/admin-product-stock-point';
import { jsPDF } from 'jspdf';


Chart.register(...registerables);

@Component({
  selector: 'app-admin-dashboard',
  standalone: false,
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard implements OnInit, AfterViewChecked {

  dashboard?: AdminDashboardResponse;

  ordersPerDay: AdminOrdersPerDayPoint[] = [];
  revenuePerDay: AdminRevenuePerDayPoint[] = [];
  topProducts: AdminTopProductPoint[] = [];
  productsByStock: AdminProductStockPoint[] = [];

  selectedChart: 'orders' | 'revenue' | 'topSold' | 'stock' = 'orders';

  loading = false;
  error = '';

  private chart?: Chart;

  chartVisible = true;

  @ViewChild('dashboardCanvas')
  dashboardCanvas?: ElementRef<HTMLCanvasElement>;

  constructor(private adminDashboardService: AdminDashboardService) { }

  ngOnInit(): void {
    this.loadDashboard();
    this.loadOrdersPerDay();
    this.loadRevenuePerDay();
    this.loadTopProducts();
    this.loadProductsByStock();
  }


  ngAfterViewChecked(): void {
    if (this.dashboardCanvas && !this.chart) {
      this.renderSelectedChart();
    }
  }

  loadDashboard(): void {
    this.loading = true;
    this.error = '';

    this.adminDashboardService.getDashboard().subscribe({
      next: (data) => {
        this.dashboard = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load dashboard data.';
        this.loading = false;
      }
    });
  }

  loadOrdersPerDay(): void {
    this.adminDashboardService.getOrdersPerDay().subscribe({
      next: (data) => {
        this.ordersPerDay = data;
        this.renderSelectedChart();
      },
      error: () => {
        this.error = 'Failed to load orders-per-day data.';
      }
    });
  }

  loadRevenuePerDay(): void {
    this.adminDashboardService.getRevenuePerDay().subscribe({
      next: (data) => {
        this.revenuePerDay = data;
        this.renderSelectedChart();
      },
      error: () => {
        this.error = 'Failed to load revenue-per-day data.';
      }
    });
  }

  loadTopProducts(): void {
    this.adminDashboardService.getTopProducts().subscribe({
      next: (data) => {
        this.topProducts = data;
        this.renderSelectedChart();
      },
      error: () => {
        this.error = 'Failed to load top-products data.';
      }
    });
  }

  loadProductsByStock(): void {
    this.adminDashboardService.getProductsByStock().subscribe({
      next: (data) => {
        this.productsByStock = data;
        this.renderSelectedChart();
      },
      error: () => {
        this.error = 'Failed to load products-by-stock data.';
      }
    });
  }

  showOrdersChart(): void {
    this.selectedChart = 'orders';
    this.animateChartSwitch();
  }

  showRevenueChart(): void {
    this.selectedChart = 'revenue';
    this.animateChartSwitch();
  }

  showTopSoldChart(): void {
    this.selectedChart = 'topSold';
    this.animateChartSwitch();
  }

  showStockChart(): void {
    this.selectedChart = 'stock';
    this.animateChartSwitch();
  }


  private renderSelectedChart(): void {
    const canvas = this.dashboardCanvas?.nativeElement;

    if (!canvas) {
      return;
    }

    if (this.chart) {
      this.chart.destroy();
      this.chart = undefined;
    }

    if (this.selectedChart === 'orders' && this.ordersPerDay.length > 0) {
      this.chart = new Chart(canvas, {
        type: 'line',
        data: {
          labels: this.ordersPerDay.map(point => point.day),
          datasets: [
            {
              label: 'Orders per Day',
              data: this.ordersPerDay.map(point => point.ordersCount),
              tension: 0.3
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false
        }
      });
    }

    if (this.selectedChart === 'revenue' && this.revenuePerDay.length > 0) {
      this.chart = new Chart(canvas, {
        type: 'line',
        data: {
          labels: this.revenuePerDay.map(point => point.day),
          datasets: [
            {
              label: 'Revenue per Day',
              data: this.revenuePerDay.map(point => point.revenue),
              tension: 0.3
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false
        }
      });
    }

    if (this.selectedChart === 'topSold' && this.topProducts.length > 0) {
      this.chart = new Chart(canvas, {
        type: 'bar',
        data: {
          labels: this.topProducts.map(point => point.name),
          datasets: [
            {
              label: 'Most Sold Products',
              data: this.topProducts.map(point => point.totalSold)
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false
        }
      });
    }

    if (this.selectedChart === 'stock' && this.productsByStock.length > 0) {
      this.chart = new Chart(canvas, {
        type: 'bar',
        data: {
          labels: this.productsByStock.map(point => point.name),
          datasets: [
            {
              label: 'Products in Stock',
              data: this.productsByStock.map(point => point.unitsInStock)
            }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false
        }
      });
    }
  }


  getChartTitle(): string {
    switch (this.selectedChart) {
      case 'orders':
        return 'Orders per Day';
      case 'revenue':
        return 'Revenue per Day';
      case 'topSold':
        return 'Top Sold Products';
      case 'stock':
        return 'Products in Stock';
      default:
        return 'Dashboard Analytics';
    }
  }


  getChartIcon(): string {
    switch (this.selectedChart) {
      case 'orders':
        return 'shopping_cart';
      case 'revenue':
        return 'attach_money';
      case 'topSold':
        return 'bar_chart';
      case 'stock':
        return 'inventory_2';
      default:
        return 'insights';
    }
  }


  // If the fade feels too subtle or too strong, change:
  // 100 for faster
  // 200 for smoother
  private animateChartSwitch(): void {
    this.chartVisible = false;

    setTimeout(() => {
      this.chartVisible = true;
      this.renderSelectedChart();
    }, 150);
  }


  exportChartAsImage(): void {
    if (!this.chart) {
      return;
    }

    const url = this.chart.toBase64Image();

    const a = document.createElement('a');
    a.href = url;
    a.download = `${this.getChartTitle().toLowerCase().replace(/\s+/g, '-')}.png`;
    a.click();
  }

  exportChartAsPdf(): void {
    if (!this.chart) {
      return;
    }

    const imageData = this.chart.toBase64Image();

    const pdf = new jsPDF('landscape');
    pdf.text(this.getChartTitle(), 14, 15);
    pdf.addImage(imageData, 'PNG', 10, 25, 260, 120);
    pdf.save(`${this.getChartTitle().toLowerCase().replace(/\s+/g, '-')}.pdf`);
  }

}