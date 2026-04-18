import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {

  constructor(private snackBar: MatSnackBar) { }

  success(message: string): void {
  this.snackBar.open(`✅ ${message}`, '', {
    duration: 3000,
    horizontalPosition: 'center',
    verticalPosition: 'top',
    panelClass: ['snackbar-success']
  });
}

error(message: string): void {
  this.snackBar.open(`❌ ${message}`, '', {
    duration: 4000,
    horizontalPosition: 'center',
    verticalPosition: 'top',
    panelClass: ['snackbar-error']
  });
}

info(message: string): void {
  this.snackBar.open(`⚠️ ${message}`, '', {
    duration: 3500,
    horizontalPosition: 'center',
    verticalPosition: 'top',
    panelClass: ['snackbar-info']
  });
}


}