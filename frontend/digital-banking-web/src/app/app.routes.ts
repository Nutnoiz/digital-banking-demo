import { Routes } from '@angular/router';

import { Login } from './features/login/login';

import { Dashboard } from './features/dashboard/dashboard';

import { Accounts } from './features/accounts/accounts';

import { Transactions } from './features/transactions/transactions';

import { Transfer } from './features/transfer/transfer';

import { Layout } from './layout/layout';

import { authGuard } from './core/guards/auth.guard';


export const routes: Routes = [

  /*
   * Public
   */

  {
    path: 'login',

    component: Login
  },


  /*
   * Protected Application
   */

  {
    path: '',

    component: Layout,

    canActivate: [
      authGuard
    ],

    children: [

      {
        path: '',

        redirectTo: 'dashboard',

        pathMatch: 'full'
      },


      {
        path: 'dashboard',

        component: Dashboard
      },


      {
        path: 'accounts',

        component: Accounts
      },


      {
        path: 'accounts/:id/transactions',

        component: Transactions
      },


      {
        path: 'transfer',

        component: Transfer
      }

    ]
  },


  /*
   * Unknown route
   */

  {
    path: '**',

    redirectTo: 'dashboard'
  }

];