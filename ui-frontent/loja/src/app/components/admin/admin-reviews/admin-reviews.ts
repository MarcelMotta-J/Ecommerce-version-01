import { Component, OnInit } from '@angular/core';
import { AdminReviewService } from '../../../services/admin/admin-review.service';
import { Review } from '../../../common/user/review';

@Component({
  selector: 'app-admin-reviews',
  standalone: false,
  templateUrl: './admin-reviews.html',
  styleUrl: './admin-reviews.css',
})
export class AdminReviews implements OnInit{

  reviews: Review[] = [];

constructor(private service: AdminReviewService) {}

ngOnInit() {
  this.load();
}

load() {
  this.service.getAll().subscribe(data => this.reviews = data);
}

delete(id: number) {
  if (confirm('⚠️ Deseja realmente excluir esta avaliação?')) {
    this.service.delete(id).subscribe(() => this.load());
  }
}

}
