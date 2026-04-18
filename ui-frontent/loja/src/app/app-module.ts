import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { MatCheckboxModule } from '@angular/material/checkbox';

import { App } from './app';

import { ProductList } from './components/product-list/product-list';
import { ProductCategoryMenu } from './components/product-category-menu/product-category-menu';
import { Search } from './components/search/search';
import { ProductDetails } from './components/product-details/product-details';


import { CartStatus } from './components/cart-status/cart-status';
import { CartDetails } from './components/cart-details/cart-details';
import { Checkout } from './components/checkout/checkout';

import { AdminProducts } from './components/admin/admin-products/admin-products';
import { AdminOrders } from './components/admin/admin-orders/admin-orders';
import { AdminOrderDetails } from './components/admin/admin-order-details/admin-order-details';
import { AdminDashboard } from './components/admin/admin-dashboard/admin-dashboard';
import { AdminLogin } from './components/admin/admin-login/admin-login';
import { AdminActivity } from './components/admin/admin-activity/admin-activity';


import { UserLogin } from './components/user/user-login/user-login';
import { UserProfile } from './components/user/user-profile/user-profile';
import { UserOrderDetail } from './components/user/user-order-detail/user-order-detail';
import { UserOrders } from './components/user/user-orders/user-orders';


import { adminAuthGuard } from './guards/admin-auth-guard';
import { authGuard } from './guards/auth-guard';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';

// bellow for Angular Material
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';

// bellow for interceptors
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './interceptors/auth-interceptor';
import { AdminCustomers } from './components/admin/admin-customers/admin-customers';
import { AdminCustomerEdit } from './components/admin/admin-customer-edit/admin-customer-edit';
import { RegisterComponent } from './components/user/register.component/register.component';
import { FavoriteComponent } from './components/user/favorite.component/favorite.component';
import { ProductCardComponent as ProductCard } from './components/shared/product-card/product-card';
import { NotificationsComponent } from './components/user/notifications/notifications.component';


// <-- Move routes into a constant
const appRoutes: Routes = [

  { path: 'cart-details', component: CartDetails },
  { path: 'products/:id', component: ProductDetails },
  { path: 'search/:keyword', component: ProductList },
  { path: 'category/:id/:name', component: ProductList },
  { path: 'category/:id', component: ProductList },
  { path: 'category', component: ProductList },
  { path: 'products', component: ProductList },


  { path: 'login', component: UserLogin },


  { path: 'admin/login', component: AdminLogin },
  // guards
  { path: 'admin/dashboard', component: AdminDashboard, canActivate: [adminAuthGuard] },
  { path: 'admin/products', component: AdminProducts, canActivate: [adminAuthGuard] },
  { path: 'admin/orders', component: AdminOrders, canActivate: [adminAuthGuard] },
  { path: 'admin/orders/:id', component: AdminOrderDetails, canActivate: [adminAuthGuard] },
  { path: 'admin/activity', component: AdminActivity, canActivate: [adminAuthGuard] },
  { path: 'admin/customers', component: AdminCustomers, canActivate: [adminAuthGuard] },
  { path: 'admin/customers/edit/:id', component: AdminCustomerEdit, canActivate: [adminAuthGuard] },

  { path: 'profile', component: UserProfile, canActivate: [authGuard] },
  { path: 'orders', component: UserOrders, canActivate: [authGuard] },
  { path: 'orders/:id', component: UserOrderDetail, canActivate: [authGuard] },
  { path: 'favorites', component: FavoriteComponent, canActivate: [authGuard] },
  { path: 'checkout', component: Checkout, canActivate: [authGuard] },
  { path: 'notifications', component: NotificationsComponent, canActivate: [authGuard] },


  { path: '', redirectTo: '/products', pathMatch: 'full' },
  { path: '**', redirectTo: '/products', pathMatch: 'full' }
];

@NgModule({
  declarations: [
    App,
    ProductList,
    ProductCategoryMenu,
    Search,
    ProductDetails,
    CartStatus,
    CartDetails,
    Checkout,
    AdminProducts,
    AdminOrders,
    AdminOrderDetails,
    AdminDashboard,
    AdminLogin,
    AdminActivity,
    AdminCustomers,
    AdminCustomerEdit,
    UserLogin,
    UserProfile,
    UserOrders,
    UserOrderDetail,
    RegisterComponent,
    FavoriteComponent,
    ProductCard,
    NotificationsComponent


  ],
  imports: [
    BrowserModule,
    NgOptimizedImage,
    CommonModule,
    NgbPaginationModule,
    RouterModule.forRoot(appRoutes), // <-- Use constant here
    ReactiveFormsModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatIconModule,
    FormsModule,
    MatMenuModule,
    MatSnackBarModule,
    MatCheckboxModule,
    MatTabsModule,
    MatCardModule,
    MatButtonModule

  ],
  providers: [
    provideHttpClient(withInterceptors([authInterceptor]))
  ],
  bootstrap: [App],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],

})
export class AppModule { }


