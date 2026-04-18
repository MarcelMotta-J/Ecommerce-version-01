import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, of } from 'rxjs';
import { Country } from '../common/country';
import { State } from '../common/state';
import { API_ENDPOINTS } from '../core/api.config';

@Injectable({
  providedIn: 'root',
})
export class LojaFormService {

  private countriesUrl = API_ENDPOINTS.COUNTRIES;
  private statesUrl = API_ENDPOINTS.STATES;


  constructor(
    private httpClient: HttpClient
  ) { }

  getCountries(): Observable<Country[]> {
    return this.httpClient.get<GetResponseCountries>(this.countriesUrl).pipe(
      map(response => response._embedded.countries)
    );
  }

  getStates(theCountryCode: string): Observable<State[]> {

    const searchStatesUrl = `${this.statesUrl}/search/findByCountryCode?code=${theCountryCode}`;
    

    return this.httpClient.get<GetResponseStates>(searchStatesUrl).pipe(
       map(response => response._embedded.states)
    );
  }

  

  getCreditCardMonths(startMonth: number): Observable<number[]> {

    let data: number[] = [];

    // build an array for "month" dropdownlist
    // - start in current month and loop until month under 12

    for (let theMonth = startMonth; theMonth <= 12; theMonth++) {
      data.push(theMonth);
    }

    return of(data);
  }

  getCreditCardYears(): Observable<number[]> {

    let data: number[] = [];

    // build an array for "year" dropdownlist
    // - start in current year and loop for next 10 years

    // get the current year
    const startYear: number = new Date().getFullYear();
    // get next 10 years
    const endYear: number = startYear + 10;

    for (let theYear = startYear; theYear <= endYear; theYear++) {
      data.push(theYear);
    }

    return of(data);
  }

}


// desembrulha json do spring data rests
interface GetResponseCountries {
  _embedded: {
    countries: Country[];
  }
}

// desembrulha json do spring data rests
interface GetResponseStates {
  _embedded: {
    states: State[];
  }
}