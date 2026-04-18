import { Component, OnInit, OnDestroy } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

import { AuthService } from './services/auth.service';
import { FavoriteService } from './services/user/favorite.service';
import { NotificationService } from './services/user/notification.service';

import { NotificationItem } from './common/user/notification-item';

import { NotificationSoundService } from './services/ui/notification-sound';
import { SnackbarService } from './services/ui/snackbar.service';
import { RealtimeNotificationService } from './services/user/realtime-notification';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
  styleUrl: './app.css'
})
export class App implements OnInit, OnDestroy {

  // Signal used to automatically close RxJS subscriptions
  private destroy$ = new Subject<void>();

  // Flag to determine whether the current route belongs to admin area
  isAdminRoute = false;

  // Reactive counters used in sidebar / header
  favoriteCount = 0;
  notificationCount = 0;

  // Full notification list used by the notification dropdown
  notifications: NotificationItem[] = [];

  showMobileCategories = false;

  constructor(
    public authService: AuthService,
    public favoriteService: FavoriteService,
    private notificationService: NotificationService,
    private router: Router,
    private notificationSoundService: NotificationSoundService,
    private snackbar: SnackbarService,
    private realtimeNotificationService: RealtimeNotificationService
  ) {

    // Listen to route changes and detect admin routes
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.isAdminRoute = this.router.url.startsWith('/admin');
      });

    // Keep favorite counter in sync with FavoriteService
    this.favoriteService.favoritesCount$
      .pipe(takeUntil(this.destroy$))
      .subscribe(count => {
        this.favoriteCount = count;
      });

    // Keep notification unread counter in sync with NotificationService
    this.notificationService.unreadCount$
      .pipe(takeUntil(this.destroy$))
      .subscribe(count => {
        this.notificationCount = count;
      });

    // Keep notification list in sync for dropdown rendering
    this.notificationService.notifications$
      .pipe(takeUntil(this.destroy$))
      .subscribe(items => {
        this.notifications = items;
      });
  }

  ngOnInit(): void {

    // On app startup, restore reactive state if user is already logged in
    if (this.authService.isLoggedIn()) {
      this.favoriteService.loadFavorites();
      this.notificationService.loadLatestNotifications();
      this.realtimeNotificationService.connect();
    }
  }

  ngOnDestroy(): void {
    console.log('App destroyed - closing websocket');

    // Disconnect realtime notifications
    this.realtimeNotificationService.disconnect();

    // Complete all subscriptions using takeUntil
    this.destroy$.next();
    this.destroy$.complete();
  }

  markNotificationAsRead(notificationId: number): void {
    // Mark a single notification as read
    this.notificationService.markAsRead(notificationId);
  }

  markAllNotificationsAsRead(): void {
    // Mark all notifications as read
    this.notificationService.markAllAsRead();
  }

  logout(): void {

    // Disconnect realtime notifications before clearing auth
    this.realtimeNotificationService.disconnect();

    // Clear authentication first
    this.authService.logout();

    // Clear local reactive state to avoid stale UI
    this.favoriteService.clearFavorites();
    this.notificationService.clearNotifications();

    // Redirect user back to store
    this.router.navigate(['/products']);
  }

  handleNotificationClick(notification: NotificationItem): void {

    // Mark as read
    this.notificationService.markAsRead(notification.id);

    // Navigate if link exists
    if (notification.link) {
      this.router.navigate([notification.link]);
    }
  }

  simulateNewNotification(): void {
    const notification: NotificationItem = {
      id: Date.now(),
      title: 'New video available',
      message: 'A channel you follow uploaded a new video.',
      read: false,
      createdAt: new Date().toISOString(),
      link: '/notifications'
    };

    this.notificationService.addNotification(notification);
    this.notificationSoundService.play();
    this.snackbar.info('New notification received');
  }

  toggleCategories(): void {
    this.showMobileCategories = !this.showMobileCategories;
  }

}