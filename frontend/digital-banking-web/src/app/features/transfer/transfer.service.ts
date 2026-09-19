import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  TransferRequest,
  TransferResponse
} from '../../core/auth/auth.models';

@Injectable({
  providedIn: 'root'
})
export class TransferService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl = '/api/transfers';

  transfer(
    request: TransferRequest
  ): Observable<TransferResponse> {

    return this.http.post<TransferResponse>(
      this.apiUrl,
      request
    );
  }
}