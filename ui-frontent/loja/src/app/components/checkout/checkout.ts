import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CartService } from '../../services/cart.service';
import { LojaFormService } from '../../services/loja-form-service';
import { Country } from '../../common/country';
import { State } from '../../common/state';
import { ShopValidators } from '../../validators/shop-validators';
import { CheckoutService } from '../../services/checkout.service';
import { Router } from '@angular/router';
import { Order } from '../../common/order';
import { OrderItem } from '../../common/order-item';
import { Purchase } from '../../common/purchase';
import { UserService } from '../../services/user/user.service';
import { UserAddressResponse } from '../../common/user/user-address-response';

@Component({
  selector: 'app-checkout',
  standalone: false,
  templateUrl: './checkout.html',
  styleUrl: './checkout.css',
})
export class Checkout implements OnInit {

  checkoutFormGroup!: FormGroup;

  totalPrice: number = 0.00;
  totalQuantity: number = 0;

  creditCardMonths: number[] = [];
  creditCardYears: number[] = [];

  countries: Country[] = [];

  shippingAddressStates: State[] = [];
  billingAddressStates: State[] = [];

  useSavedProfile = false;

  useSavedAddress = true;

  sameAsShipping = true;

  savedAddresses: UserAddressResponse[] = [];
  defaultAddress: UserAddressResponse | null = null;
  selectedAddress: UserAddressResponse | null = null;

  showNewAddressForm = false;



  constructor(
    private formBuilder: FormBuilder,
    private cartService: CartService,
    private lojaFormService: LojaFormService,
    private checkoutService: CheckoutService,
    private userService: UserService,
    private router: Router
  ) { }

  ngOnInit(): void {

    this.reviewCartDetails();



    this.checkoutFormGroup = this.formBuilder.group({
      customer: this.formBuilder.group({
        firstName: ['', [Validators.required, Validators.minLength(2), ShopValidators.notOnlyWhitespace]],
        lastName: ['', [Validators.required, Validators.minLength(2), ShopValidators.notOnlyWhitespace]],
        email: ['', [Validators.required, Validators.email]]
      }),

      shippingAddress: this.formBuilder.group({
        street: ['', [Validators.required, Validators.minLength(2), ShopValidators.notOnlyWhitespace]],
        city: ['', [Validators.required, Validators.minLength(2), ShopValidators.notOnlyWhitespace]],
        state: ['', [Validators.required, Validators.minLength(2)]],
        country: ['', [Validators.required, Validators.minLength(2)]],
        zipCode: ['', [Validators.required, Validators.minLength(5), ShopValidators.notOnlyWhitespace]]
      }),

      billingAddress: this.formBuilder.group({
        street: ['', [Validators.required, Validators.minLength(2), ShopValidators.notOnlyWhitespace]],
        city: ['', [Validators.required, Validators.minLength(2), ShopValidators.notOnlyWhitespace]],
        state: ['', [Validators.required, Validators.minLength(2)]],
        country: ['', [Validators.required, Validators.minLength(2)]],
        zipCode: ['', [Validators.required, Validators.minLength(5), ShopValidators.notOnlyWhitespace]],
      }),

      creditCard: this.formBuilder.group({
        cardType: ['', [Validators.required]],
        nameOnCard: ['', [Validators.required, ShopValidators.notOnlyWhitespace]],
        cardNumber: ['', [Validators.required, Validators.pattern('^[0-9]{16}$'), ShopValidators.notOnlyWhitespace]],
        securityCode: ['', [Validators.required, Validators.pattern('^[0-9]{3,4}$'), ShopValidators.notOnlyWhitespace]],
        expirationMonth: ['', [Validators.required]],
        expirationYear: ['', [Validators.required]]
      })
    });

    this.setupShippingBillingSync();

    this.loadUserData();

    // populate credit card months
    const startMonth: number = new Date().getMonth() + 1;
    console.log(' startMonth = ' + startMonth);

    this.lojaFormService.getCreditCardMonths(startMonth).subscribe(
      data => {
        console.log('retrieved credit card months: ' + JSON.stringify(data));
        this.creditCardMonths = data;
      }
    );

    // populate credit card years
    this.lojaFormService.getCreditCardYears().subscribe(
      data => {
        console.log('retrieved credit card years: ' + JSON.stringify(data));
        this.creditCardYears = data;
      }
    );

    // populate countries
    this.lojaFormService.getCountries().subscribe(
      data => {
        console.log('retrieved countries : ' + JSON.stringify(data));
        this.countries = data;

        // Load saved user data only after countries are available
        //this.loadUserDataIntoCheckout();
      }
    );

    this.userService.getAddresses().subscribe(addresses => {
      this.defaultAddress = addresses.find(a => a.isDefault) || null;

      if (this.defaultAddress) {
        this.loadDefaultAddress();
      }
    });
  }


  setupShippingBillingSync(): void {
    this.checkoutFormGroup.get('shippingAddress')?.valueChanges.subscribe(value => {
      if (!this.sameAsShipping) {
        return;
      }

      // copia campos simples
      this.checkoutFormGroup.get('billingAddress')?.patchValue({
        street: value.street,
        city: value.city,
        zipCode: value.zipCode,
        country: value.country
      }, { emitEvent: false });

      const countryCode = value.country?.code;
      const selectedState = value.state;

      if (!countryCode) {
        this.billingAddressStates = [];
        this.checkoutFormGroup.get('billingAddress.state')?.setValue(null, { emitEvent: false });
        return;
      }

      this.lojaFormService.getStates(countryCode).subscribe((states: State[]) => {
        this.billingAddressStates = states;

        const matchedState = states.find(s => s.name === selectedState?.name);

        if (matchedState) {
          this.checkoutFormGroup.get('billingAddress.state')?.setValue(matchedState, { emitEvent: false });
        }
      });
    });
  }

  loadUserDataIntoCheckout(): void {
    this.userService.getProfile().subscribe({
      next: profile => {
        this.userService.getAddresses().subscribe({
          next: addresses => {
            const defaultAddress = addresses.find(a => a.isDefault);

            // Fill customer data
            this.checkoutFormGroup.patchValue({
              customer: {
                firstName: profile.firstName,
                lastName: profile.lastName,
                email: profile.email
              }
            });

            // If user has no default address, stop here
            if (!defaultAddress) {
              return;
            }

            // Fill simple shipping fields first
            this.checkoutFormGroup.get('shippingAddress')?.patchValue({
              street: defaultAddress.street,
              city: defaultAddress.city,
              zipCode: defaultAddress.zipCode
            });

            // Resolve country + state objects for shipping
            this.setShippingCountryAndState(defaultAddress);
          },
          error: err => {
            console.error('Error loading addresses', err);
          }
        });
      },
      error: err => {
        console.error('Error loading profile', err);
      }
    });
  }

  setShippingCountryAndState(address: UserAddressResponse): void {
    const country = this.countries.find(c => c.name === address.country);

    if (!country) return;

    // Set country object in the form
    this.checkoutFormGroup.get('shippingAddress.country')?.setValue(country);

    // Load states using country code
    this.lojaFormService.getStates(country.code).subscribe(states => {
      this.shippingAddressStates = states;

      const state = states.find(s => s.name === address.state);

      if (state) {
        this.checkoutFormGroup.get('shippingAddress.state')?.setValue(state);
      }
    });
  }



  setBillingCountryAndState(address: UserAddressResponse): void {
    const country = this.countries.find(c => c.name === address.country);

    if (!country) return;

    this.checkoutFormGroup.get('billingAddress.country')?.setValue(country);

    this.lojaFormService.getStates(country.code).subscribe(states => {
      this.billingAddressStates = states;

      const state = states.find(s => s.name === address.state);

      if (state) {
        this.checkoutFormGroup.get('billingAddress.state')?.setValue(state);
      }
    });
  }

  onSubmit() {

    if (this.checkoutFormGroup.invalid) {
      this.checkoutFormGroup.markAllAsTouched();
      return;
    }

    console.log(`handling the submition button`);
    console.log(this.checkoutFormGroup.get('customer')?.value);
    console.log('The email is ' + this.checkoutFormGroup.get('customer')?.value.email);

    console.log('The shipping address country is '
      + this.checkoutFormGroup.get('shippingAddress')?.value.country.name);

    console.log('The shipping address state is '
      + this.checkoutFormGroup.get('shippingAddress')?.value.state.name);

    // set up order
    let order = new Order();
    order.totalPrice = this.totalPrice;
    order.totalQuantity = this.totalQuantity;

    // get cart items
    const cartItems = this.cartService.cartItems;

    // create orderItems from cartItems
    let orderItems: OrderItem[] = cartItems.map(tempCartItem => new OrderItem(tempCartItem));

    // set up purchase
    let purchase = new Purchase();

    // populate purchase - customer
    purchase.customer = this.checkoutFormGroup.controls['customer'].value;

    // populate purchase - shipping address
    purchase.shippingAddress = this.checkoutFormGroup.controls['shippingAddress'].value;
    const shippingState: State = JSON.parse(JSON.stringify(purchase.shippingAddress.state));
    const shippingCountry: Country = JSON.parse(JSON.stringify(purchase.shippingAddress.country));
    purchase.shippingAddress.state = shippingState.name;
    purchase.shippingAddress.country = shippingCountry.name;

    // populate purchase - billing address
    purchase.billingAddress = this.checkoutFormGroup.controls['billingAddress'].value;
    const billingState: State = JSON.parse(JSON.stringify(purchase.billingAddress.state));
    const billingCountry: Country = JSON.parse(JSON.stringify(purchase.billingAddress.country));
    purchase.billingAddress.state = billingState.name;
    purchase.billingAddress.country = billingCountry.name;

    // populate purchase - order and orderItems
    purchase.order = order;
    purchase.orderItems = orderItems;

    // call rest api via checkoutService
    this.checkoutService.placeOrder(purchase).subscribe({
      next: response => {
        alert(`Your order has been received.\nOrder tracking number: ${response.orderTrackingNumber}`);

        // reset cart
        this.resetCard();
      },
      error: err => {
        alert(`There was an error: ${err.message}`);
      }
    });
  }

  resetCard() {
    // reset cart data
    this.cartService.cartItems = [];
    this.cartService.totalPrice.next(0);
    this.cartService.totalQuantity.next(0);

    // reset form data
    this.checkoutFormGroup.reset();

    // navigate back to the products page
    this.router.navigateByUrl('/products');
  }

  /*
  copyShippingAddressToBillingAddress(event: any) {
    if (event.target.checked) {
      const shippingAddress = this.checkoutFormGroup.get('shippingAddress')?.value;

      this.checkoutFormGroup.get('billingAddress')?.patchValue({
        street: shippingAddress.street,
        city: shippingAddress.city,
        zipCode: shippingAddress.zipCode,
        country: shippingAddress.country
      });

      const selectedShippingState = shippingAddress.state;

      const countryCode = shippingAddress.country?.code;
      if (!countryCode) return;

      this.lojaFormService.getStates(countryCode).subscribe(data => {
        this.billingAddressStates = data;

        const matchedState = data.find(state => state.name === selectedShippingState?.name);

        if (matchedState) {
          this.checkoutFormGroup.get('billingAddress.state')?.setValue(matchedState);
        }
      });

    } else {
      this.checkoutFormGroup.get('billingAddress')?.reset();
      this.billingAddressStates = [];
    }
  }
    */

  /*
  copyShippingAddressToBillingAddress(event: any) {
    if (event.target.checked) {
      const shippingAddress = this.checkoutFormGroup.get('shippingAddress')?.value;

      this.checkoutFormGroup.get('billingAddress')?.patchValue({
        street: shippingAddress.street,
        city: shippingAddress.city,
        zipCode: shippingAddress.zipCode,
        country: shippingAddress.country
      });

      const selectedShippingState = shippingAddress.state;
      const countryCode = shippingAddress.country?.code;

      if (!countryCode) return;

      this.lojaFormService.getStates(countryCode).subscribe(data => {
        this.billingAddressStates = data;

        const matchedState = data.find(state => state.name === selectedShippingState?.name);

        if (matchedState) {
          this.checkoutFormGroup.get('billingAddress.state')?.setValue(matchedState);
        }
      });

    } else {
      this.checkoutFormGroup.get('billingAddress')?.reset();
      this.billingAddressStates = [];
    }
  }
  */

  copyShippingAddressToBillingAddress(event: any): void {

    if (event.target.checked) {

      const shipping = this.checkoutFormGroup.get('shippingAddress')?.value;

      // copia dados simples
      this.checkoutFormGroup.get('billingAddress')?.patchValue({
        street: shipping.street,
        city: shipping.city,
        zipCode: shipping.zipCode,
        country: shipping.country
      });

      // resolve state corretamente
      const selectedState = shipping.state;
      const countryCode = shipping.country?.code;

      if (!countryCode) return;

      this.lojaFormService.getStates(countryCode).subscribe(data => {

        this.billingAddressStates = data;

        const matchedState = data.find(s => s.name === selectedState?.name);

        if (matchedState) {
           this.checkoutFormGroup.get('billingAddress.state')?.setValue(matchedState, { emitEvent: false });
        }

        // 🔥 bloqueia edição
        this.checkoutFormGroup.get('billingAddress')?.disable();
      });

    } else {

      // 🔥 libera edição novamente
      this.checkoutFormGroup.get('billingAddress')?.enable();

      // opcional: limpar
      this.checkoutFormGroup.get('billingAddress')?.reset();
      this.billingAddressStates = [];
    }
  }

  reviewCartDetails() {
    this.cartService.totalPrice.subscribe(
      data => this.totalPrice = data
    );

    this.cartService.totalQuantity.subscribe(
      data => this.totalQuantity = data
    );
  }

  handleMonthsAndYears() {
    const creditCardFormGroup = this.checkoutFormGroup.get('creditCard');

    const currentYear: number = new Date().getFullYear();
    const selectedYear: number = Number(creditCardFormGroup?.value.expirationYear);

    let startMonth: number;

    if (currentYear === selectedYear) {
      startMonth = new Date().getMonth() + 1;
    } else {
      startMonth = 1;
    }

    this.lojaFormService.getCreditCardMonths(startMonth).subscribe(
      data => {
        console.log(` retrieved credit card months = ` + JSON.stringify(data));
        this.creditCardMonths = data;
      }
    );
  }


  toggleSavedProfile(event: any): void {
    const checked = event.target.checked;
    this.useSavedProfile = checked;

    if (checked) {

      // garante que countries já carregaram
      if (this.countries.length === 0) {
        console.warn('Countries not loaded yet, retrying...');

        this.lojaFormService.getCountries().subscribe(data => {
          this.countries = data;
          this.loadUserDataIntoCheckout();
        });

      } else {
        this.loadUserDataIntoCheckout();
      }
    }
  }


  loadDefaultAddress(): void {

    if (!this.defaultAddress) {
      return;
    }

    const address = this.defaultAddress;

    this.checkoutFormGroup.patchValue({
      shippingAddress: {
        street: address.street,
        city: address.city,
        state: address.state,
        country: address.country,
        zipCode: address.zipCode
      }
    });

    // carregar states baseado no country
    this.loadStatesFromAddress(address);
  }


  loadStatesFromAddress(address: any): void {
    const country = this.countries.find(c => c.name === address.country);

    if (!country) {
      return;
    }

    this.checkoutFormGroup.get('shippingAddress.country')?.setValue(country);

    this.lojaFormService.getStates(country.code).subscribe((states: State[]) => {
      this.shippingAddressStates = states;

      const state = states.find(s => s.name === address.state);

      if (state) {
        this.checkoutFormGroup.get('shippingAddress.state')?.setValue(state);
      }
    });
  }

  getCountryCode(countryName: string): string | null {
    const country = this.countries.find(c => c.name === countryName);
    return country ? country.code : null;
  }

  onToggleSavedAddress(): void {
    if (this.useSavedAddress) {
      this.loadDefaultAddress();
      this.showNewAddressForm = false;
    } else {
      this.checkoutFormGroup.get('shippingAddress')?.reset();
      this.showNewAddressForm = true;
    }
  }

  copyShippingToBilling(): void {
    if (this.sameAsShipping) {
      this.checkoutFormGroup.patchValue({
        billingAddress: this.checkoutFormGroup.get('shippingAddress')?.value
      });
    }
  }

  loadUserData(): void {
    this.userService.getAddresses().subscribe(addresses => {

      this.savedAddresses = addresses;

      this.defaultAddress = addresses.find(a => a.isDefault) || null;

      if (this.defaultAddress) {
        this.fillShippingFromAddress(this.defaultAddress);
      }

    });
  }

  fillShippingFromAddress(address: any): void {

    this.checkoutFormGroup.patchValue({
      shippingAddress: {
        street: address.street,
        city: address.city,
        zipCode: address.zipCode
      }
    });

    const country = this.countries.find(c => c.name === address.country);

    if (country) {
      this.checkoutFormGroup.get('shippingAddress.country')?.setValue(country);

      this.lojaFormService.getStates(country.code).subscribe(states => {
        this.shippingAddressStates = states;

        const state = states.find(s => s.name === address.state);

        if (state) {
          this.checkoutFormGroup.get('shippingAddress.state')?.setValue(state);
        }
      });
    }
  }


  selectAddress(address: any): void {
    this.selectedAddress = address;
    this.fillShippingFromAddress(address);

    this.showNewAddressForm = false;
  }

  openNewAddressForm(): void {
    this.showNewAddressForm = true;
    this.useSavedAddress = false;
    this.selectedAddress = null;

    this.checkoutFormGroup.get('shippingAddress')?.reset();
    this.shippingAddressStates = [];
  }


  get creditCardForm() {
    return this.checkoutFormGroup.get('creditCard');
  }

  get shippingAddressForm(): FormGroup {
    return this.checkoutFormGroup.get('shippingAddress') as FormGroup;
  }

  compareCountries(c1: Country, c2: Country): boolean {
    return c1 && c2 ? c1.id === c2.id : c1 === c2;
  }

  /*
  getStates(formGroupName: 'shippingAddress' | 'billingAddress') {
    const formGroup = this.checkoutFormGroup.get(formGroupName) as FormGroup;

    const countryId = formGroup.value.country?.code;

    if (!countryId) return;

    this.lojaFormService.getStates(countryId).subscribe(data => {
      if (formGroupName === 'shippingAddress') {
        this.shippingAddressStates = data;
      } else {
        this.billingAddressStates = data;
      }

      //formGroup.get('state')?.setValue(data[0]);
      const currentState = formGroup.get('state')?.value;

      if (!currentState) {
        formGroup.get('state')?.setValue(data[0]);
      }


    });
  }
    */

  getStates(formGroupName: 'shippingAddress' | 'billingAddress') {
    const formGroup = this.checkoutFormGroup.get(formGroupName) as FormGroup;

    const countryCode = formGroup.value.country?.code;

    if (!countryCode) return;

    this.lojaFormService.getStates(countryCode).subscribe(data => {
      if (formGroupName === 'shippingAddress') {
        this.shippingAddressStates = data;
      } else {
        this.billingAddressStates = data;
      }

      const currentState = formGroup.get('state')?.value;

      if (!currentState) {
        formGroup.get('state')?.setValue(data[0]);
      }
    });
  }



  // customer
  get customerFirstName() {
    return this.checkoutFormGroup.get('customer.firstName');
  }

  get customerLastName() {
    return this.checkoutFormGroup.get('customer.lastName');
  }

  get customerEmail() {
    return this.checkoutFormGroup.get('customer.email');
  }

  // shipping
  get shippingCountry() {
    return this.checkoutFormGroup.get('shippingAddress.country');
  }

  get shippingStreet() {
    return this.checkoutFormGroup.get('shippingAddress.street');
  }

  get shippingCity() {
    return this.checkoutFormGroup.get('shippingAddress.city');
  }

  get shippingState() {
    return this.checkoutFormGroup.get('shippingAddress.state');
  }

  get shippingZipCode() {
    return this.checkoutFormGroup.get('shippingAddress.zipCode');
  }

  // billing
  get billingCountry() {
    return this.checkoutFormGroup.get('billingAddress.country');
  }

  get billingStreet() {
    return this.checkoutFormGroup.get('billingAddress.street');
  }

  get billingCity() {
    return this.checkoutFormGroup.get('billingAddress.city');
  }

  get billingState() {
    return this.checkoutFormGroup.get('billingAddress.state');
  }

  get billingZipCode() {
    return this.checkoutFormGroup.get('billingAddress.zipCode');
  }

  // credit card
  get creditCardCardType() {
    return this.checkoutFormGroup.get('creditCard.cardType');
  }

  get creditCardNameOnCard() {
    return this.checkoutFormGroup.get('creditCard.nameOnCard');
  }

  get creditCardCardNumber() {
    return this.checkoutFormGroup.get('creditCard.cardNumber');
  }

  get creditCardSecurityCode() {
    return this.checkoutFormGroup.get('creditCard.securityCode');
  }

  get creditCardExpirationMonth() {
    return this.checkoutFormGroup.get('creditCard.expirationMonth');
  }

  get creditCardExpirationYear() {
    return this.checkoutFormGroup.get('creditCard.expirationYear');
  }
}