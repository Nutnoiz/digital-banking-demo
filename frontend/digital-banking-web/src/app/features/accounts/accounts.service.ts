import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Account } from '../../core/auth/auth.models';

@Injectable({
  providedIn: 'root'
})
export class AccountsService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl = '/api/accounts';

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl);
  }

  getAccountById(id: number): Observable<Account> {
    return this.http.get<Account>(`${this.apiUrl}/${id}`);
  }
}