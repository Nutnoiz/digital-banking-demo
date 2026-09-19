import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import {
  BankTransaction
} from '../../core/auth/auth.models';

@Injectable({
  providedIn: 'root'
})
export class TransactionsService {

  private readonly http = inject(HttpClient);

  getTransactions(
    accountId: number
  ): Observable<BankTransaction[]> {

    return this.http.get<BankTransaction[]>(
      `/api/accounts/${accountId}/transactions`
    );
  }
}