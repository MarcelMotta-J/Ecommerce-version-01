import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';

import { AuthService } from '../auth.service';
import { NotificationService } from './notification.service';
import { NotificationSoundService } from '../ui/notification-sound';
import { SnackbarService } from '../ui/snackbar.service';
import { NotificationItem } from '../../common/user/notification-item';

import { WS_URL } from '../../core/api.config';

@Injectable({
  providedIn: 'root'
})
export class RealtimeNotificationService {

  private client: Client | null = null;
  private connected = false;

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private notificationSoundService: NotificationSoundService,
    private snackbar: SnackbarService
  ) { 

    
  }

  connect(): void {
    const token = this.authService.getToken();
    if (!token || !this.authService.isLoggedIn() || this.connected) {
      console.log('Realtime connect skipped');
      return;
    }

    console.log('Connecting realtime notifications...');

    this.client = new Client({
      //brokerURL: 'ws://localhost:8080/ws-notifications',
      //brokerURL: `${window.location.protocol === 'https:' ? 'wss' : 'ws'}://${window.location.host}/ws-notifications`,
      brokerURL: WS_URL,
      reconnectDelay: 5000,

      connectHeaders: {
        //Authorization: `Bearer ${this.authService.getToken()}`
        Authorization: `Bearer ${token}`
      },

      onConnect: () => {
        console.log('Realtime connected successfully');
        this.connected = true;

        this.client?.subscribe('/user/queue/notifications', message => {
          console.log('Realtime notification received:', message.body);


          const notification: NotificationItem = JSON.parse(message.body);

          this.notificationService.addNotification(notification);
          this.notificationSoundService.play();
          this.snackbar.info(notification.title);
        });
      },

      onDisconnect: () => {
        console.log('Realtime disconnected');
        this.connected = false;
      },

      onStompError: frame => {
        console.error('STOMP error', frame);
      },

      onWebSocketError: error => {
        console.error('WebSocket error', error);
      }
    });

    this.client.activate();
  }

  disconnect(): void {
    this.client?.deactivate();
    this.client = null;
    this.connected = false;
  }
}
