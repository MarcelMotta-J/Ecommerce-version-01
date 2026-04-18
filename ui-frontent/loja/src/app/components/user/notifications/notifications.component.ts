import { Component, OnInit } from '@angular/core';


import { NotificationService } from '../../../services/user/notification.service';
import { NotificationItem } from '../../../common/user/notification-item';

@Component({
  selector: 'app-notifications',
  standalone: false,
  templateUrl: './notifications.html',
  styleUrl: './notifications.css'
})
export class NotificationsComponent implements OnInit {

  // Full notification list displayed on the page
  notifications: NotificationItem[] = [];

  // Reactive unread counter displayed in the header area
  unreadCount = 0;

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    // Keep the page list synchronized with NotificationService
    this.notificationService.notifications$.subscribe(items => {
      this.notifications = items;
    });

    // Keep unread badge synchronized
    this.notificationService.unreadCount$.subscribe(count => {
      this.unreadCount = count;
    });

    // Load mock notifications if list is empty
    if (this.notifications.length === 0) {
      this.notificationService.loadNotifications();
    }
  }

  markAsRead(notificationId: number): void {
    this.notificationService.markAsRead(notificationId);
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead();
  }
}
