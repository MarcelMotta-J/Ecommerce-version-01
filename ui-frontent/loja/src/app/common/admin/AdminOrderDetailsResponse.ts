export interface AdminOrderDetailsResponse {

  id: number;
  trackingNumber: string;
  status: string;

  totalPrice: number;
  totalQuantity: number;

  dateCreated: string;
  dateUpdated: string;

  userEmail: string | null;

  customer: {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
  };

  shippingAddress: {
    street: string;
    city: string;
    state: string;
    country: string;
    zipCode: string;
  };

  billingAddress: {
    street: string;
    city: string;
    state: string;
    country: string;
    zipCode: string;
  };

  items: {
    id: number;
    productId: number;
    quantity: number;
    unitPrice: number;
    imageUrl: string;
  }[];

}