export interface UserAddressResponse {
    id?: number;
    street: string;
    city: string;
    state: string;
    country: string;
    zipCode: string;
    isDefault: boolean;
}
