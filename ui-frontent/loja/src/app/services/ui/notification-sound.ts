
import { Injectable } from '@angular/core';
import { NotificationItem } from '../../common/user/notification-item';

@Injectable({
  providedIn: 'root'
})
@Injectable({
  providedIn: 'root'
})
export class NotificationSoundService {

  private audio = new Audio('assets/sounds/notification.mp3');

  play(): void {
    this.audio.currentTime = 0;
    this.audio.play().catch(err => {
      console.warn('Notification sound could not be played', err);
    });
  }
}