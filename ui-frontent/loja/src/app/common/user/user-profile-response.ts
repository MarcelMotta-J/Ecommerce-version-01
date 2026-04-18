export interface UserProfileResponse {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    phoneNumber: string;
    enabled: boolean;
    createdAt: string;
    roles: string[];
}
