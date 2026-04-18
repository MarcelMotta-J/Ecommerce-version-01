import { Component, OnInit } from '@angular/core';

import { AdminActionLogRow } from '../../../common/admin/admin-action-log-row';
import { AdminActivityService } from '../../../services/admin/admin-activity.service';


@Component({
  selector: 'app-admin-activity',
  standalone: false,
  templateUrl: './admin-activity.html',
  styleUrl: './admin-activity.css',
})
export class AdminActivity implements OnInit {

  logs: AdminActionLogRow[] = [];

  loading = false;
  error = '';

  page = 0;
  size = 5;
  totalElements = 0;
  totalPages = 0;

  constructor(private adminActivityService: AdminActivityService) { }

  ngOnInit(): void {
    this.loadActivity();
  }

  loadActivity(): void {
    this.loading = true;
    this.error = '';

    this.adminActivityService.getActivity(this.page, this.size).subscribe({
      next: (response) => {
        this.logs = response.content;
        this.page = response.page;
        this.size = response.size;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load admin activity.';
        this.loading = false;
      }
    });
  }

  exportTxt(): void {
    this.adminActivityService.exportActivityTxt().subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'admin-activity.txt';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => {
        this.error = 'Failed to export admin activity TXT.';
      }
    });
  }

  exportPdf(): void {
    this.adminActivityService.exportActivityPdf().subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'admin-activity.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => {
        this.error = 'Failed to export admin activity PDF.';
      }
    });
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadActivity();
    }
  }

  nextPage(): void {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.loadActivity();
    }
  }

}
