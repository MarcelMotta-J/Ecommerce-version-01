export interface AdminOrderRow {
  id: number;
  trackingNumber: string;
  userEmail: string | null;
  customerEmail: string | null;
  status: string;
  totalPrice: number;
  totalQuantity: number;
  dateCreated: string;
}