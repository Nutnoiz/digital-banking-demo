import {
  Component,
  OnInit,
  ChangeDetectorRef,
  inject
} from '@angular/core';

import {
  ActivatedRoute,
  RouterLink
} from '@angular/router';

import { DecimalPipe } from '@angular/common';

import { TransactionsService } from './transactions.service';

import {
  BankTransaction
} from '../../core/auth/auth.models';

import {
  HttpErrorService
} from '../../core/http/http-error.service';


@Component({
  selector: 'app-transactions',

  standalone: true,

  imports: [
    RouterLink,
    DecimalPipe
  ],

  templateUrl: './transactions.html',

  styleUrl: './transactions.css'
})
export class Transactions implements OnInit {

  private readonly route =
    inject(ActivatedRoute);

  private readonly service =
    inject(TransactionsService);

  private readonly httpErrorService =
    inject(HttpErrorService);

  private readonly changeDetectorRef =
    inject(ChangeDetectorRef);


  accountId = 0;

  transactions: BankTransaction[] = [];

  loading = true;

  errorMessage = '';


  ngOnInit(): void {

    console.log(
      'TRANSACTIONS STEP 1: ngOnInit()'
    );


    const id =
      this.route.snapshot.paramMap.get('id');


    console.log(
      'TRANSACTIONS STEP 2: route id =',
      id
    );


    this.accountId = Number(id);


    console.log(
      'TRANSACTIONS STEP 3: accountId =',
      this.accountId
    );


    if (!this.accountId) {

      this.errorMessage =
        'Invalid account ID';

      this.loading = false;

      this.changeDetectorRef.detectChanges();

      return;
    }


    this.loadTransactions();

  }


  loadTransactions(): void {

    console.log(
      'TRANSACTIONS STEP 4: calling API'
    );


    console.log(
      'GET:',
      `/api/accounts/${this.accountId}/transactions`
    );


    this.loading = true;

    this.errorMessage = '';


    this.service
      .getTransactions(this.accountId)
      .subscribe({

        next: (transactions) => {

          console.log(
            'TRANSACTIONS STEP 5: API response:',
            transactions
          );

          console.table(transactions);


          this.transactions =
            transactions;

          this.loading = false;


          console.log(
            'TRANSACTIONS STEP 6: loading =',
            this.loading
          );


          console.log(
            'TRANSACTIONS STEP 7: count =',
            this.transactions.length
          );


          this.changeDetectorRef.detectChanges();

        },


        error: (error) => {

          console.error(
            'TRANSACTIONS API ERROR:',
            error
          );


          this.errorMessage =
            this.httpErrorService.getMessage(
              error,
              'Unable to load transactions.'
            );


          this.loading = false;


          this.changeDetectorRef.detectChanges();

        }

      });

  }

}