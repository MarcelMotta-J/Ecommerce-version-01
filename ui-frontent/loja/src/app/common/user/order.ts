export interface Order {

    id: number;
    trackingNumber: string;
    status: string;
    totalPrice: number;
    totalQuantity: number;
    dateCreated: string;
    itemCount: number;
}
