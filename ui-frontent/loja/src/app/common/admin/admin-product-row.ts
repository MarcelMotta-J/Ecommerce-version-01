export interface AdminProductRow {
  id: number;
  categoryId: number;
  categoryName: string;
  sku: string;
  name: string;
  description: string;
  unitPrice: number;
  imageUrl: string;
  active: boolean;
  unitsInStock: number;
  dateCreated: string;
  lastUpdated: string | null;
}