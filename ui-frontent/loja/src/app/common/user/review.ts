export interface Review {
  id: number;
  rating: number;
  comment: string;
  createdAt: Date;
  productId: number;
  productName: string;
  userId: number;
  userEmail: string;
}