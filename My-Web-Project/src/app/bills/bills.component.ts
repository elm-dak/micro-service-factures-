import { Component } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-bills',
  standalone: false,

  templateUrl: './bills.component.html',
  styleUrl: './bills.component.css'
})
export class BillsComponent {
  bills: any;
  customerId!:number;
  constructor(private http: HttpClient,private router:Router,private route:ActivatedRoute) {
    this.customerId = this.route.snapshot.params['customerId'];
  }

  ngOnInit() {
    this.http.get('http://localhost:8888/BILLING-SERVICE/api/bills/search/byCustomerId?projection=fullBills&customerId='+this.customerId).subscribe({
      next: (data) => {
        this.bills = data;
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des produits:', err);
      }
    });
  }


  getBillsDetails(b: any) {
    this.router.navigateByUrl(/bill-details/+b.id);

  }
}
