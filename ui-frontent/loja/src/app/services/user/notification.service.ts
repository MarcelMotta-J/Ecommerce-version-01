import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

import { NotificationItem } from '../../common/user/notification-item';

import { API_ENDPOINTS } from '../../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {

  //private baseUrl = API_ENDPOINTS.WS_NOTIFICATIONS;
  private baseUrl = API_ENDPOINTS.USER_NOTIFICATIONS;

  private notificationsSubject = new BehaviorSubject<NotificationItem[]>([]);
  notifications$ = this.notificationsSubject.asObservable();

  private unreadCountSubject = new BehaviorSubject<number>(0);
  unreadCount$ = this.unreadCountSubject.asObservable();

  constructor(private http: HttpClient) { }


  loadNotifications(): void {
    this.getNotifications().subscribe({
      next: data => {
        this.notificationsSubject.next(data);
        this.emitUnreadCount(data);
      },
      error: err => {
        console.error('Error loading notifications', err);
      }
    });
  }

  loadLatestNotifications(): void {
    this.getLatestNotifications().subscribe({
      next: data => {
        this.notificationsSubject.next(data);
        this.emitUnreadCount(data);
      },
      error: err => {
        console.error('Error loading latest notifications', err);
      }
    });
  }

  // list last 10 notifications
  getLatestNotifications(): Observable<NotificationItem[]> {
    return this.http.get<NotificationItem[]>(`${this.baseUrl}/latest`);
  }

  // list all notifications
  getNotifications(): Observable<NotificationItem[]> {
    return this.http.get<NotificationItem[]>(this.baseUrl);
  }

  markAsRead(notificationId: number): void {
    this.http.patch<void>(`${this.baseUrl}/${notificationId}/read`, {}).subscribe({
      next: () => {
        const updated = this.notificationsSubject.value.map(notification =>
          notification.id === notificationId
            ? { ...notification, read: true }
            : notification
        );

        this.notificationsSubject.next(updated);
        this.emitUnreadCount(updated);
      },
      error: err => {
        console.error('Error marking notification as read', err);
      }
    });
  }

  markAllAsRead(): void {
    this.http.patch<void>(`${this.baseUrl}/read-all`, {}).subscribe({
      next: () => {
        const updated = this.notificationsSubject.value.map(notification => ({
          ...notification,
          read: true
        }));

        this.notificationsSubject.next(updated);
        this.emitUnreadCount(updated);
      },
      error: err => {
        console.error('Error marking all notifications as read', err);
      }
    });
  }


  addNotification(notification: NotificationItem): void {
    const current = this.notificationsSubject.value;

    const alreadyExists = current.some(item => item.id === notification.id);

    if (alreadyExists) {
      return;
    }

    const updated = [notification, ...current].slice(0, 10);
    this.notificationsSubject.next(updated);
    this.emitUnreadCount(updated);
  }

  clearNotifications(): void {
    this.notificationsSubject.next([]);
    this.unreadCountSubject.next(0);
  }

  private emitUnreadCount(notifications: NotificationItem[]): void {
    const unreadCount = notifications.filter(notification => !notification.read).length;
    this.unreadCountSubject.next(unreadCount);
  }
}