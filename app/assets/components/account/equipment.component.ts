import {Component} from "@angular/core"
import {Injectable} from '@angular/core';
import {Http, Headers, Response, RequestOptions} from '@angular/http';
import {OnInit} from '@angular/core';
import {AlertService, AuthenticationService} from "../../services/index";

import {MyEvent} from '../../models/event';

@Component({
  selector: "todo-account-equipment",
  templateUrl: "assets/components/account/equipment.component.html"
})

@Injectable()
export class EquipmentComponent implements OnInit {


  equipments: any = [];
  equipmentsCopy: any = [];
  equipmentUnits: any = [];
  addEquipments: number = 1;
  newequipment: any = {};
  labs: any = [];
  ManagerLabs: any = [];
  roleId: any;
  equipcat: number = 0;


  events: any = [];

  header: any = {};
  searchTerm : any;
  event: MyEvent;

  constructor(public http: Http, private authService: AuthenticationService) {

  }

  getLabs() {
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});
    this.authService.getAllLabs(this.roleId, this.authService.username)
      .subscribe((result) => {
        this.labs = (result as any).labs;
        this.ManagerLabs = [];
        for (let i = 0; i < this.labs.length; i++) {
          this.ManagerLabs.push(this.labs[i].id);
        }
        let arrLabId: any = [];
        for (let i = 0; i < this.labs.length; i++) {
          arrLabId.push(this.labs[i].labId.id);
        }
        this.authService.getEquipments(arrLabId)
          .subscribe((result) => {
            this.equipments = result.equipments;
            this.equipmentsCopy = result.equipments;
            this.equipmentUnits = result.units;
          });
      });
  }

  ngOnInit() {
    this.addEquipments = 1;
    this.authService.getRoleandMenuData(this.authService.username)
      .subscribe((result) => {
        this.roleId = result.userDetails.roleId.id;
        this.getLabs();
      });
    this.events = [];

    this.header = {
      left: 'prev,next, today',
      center: 'title',
      right: 'month,agendaWeek,agendaDay'
    };
  }

  insertequipment() {
    this.authService.addEquipment(this.newequipment)
      .subscribe((result) => {
        // let labId = result.userDetails.labId.id;
        this.ngOnInit();
      });
  }

  dropChange(val: any) {
  }

  addEquipmentsClick() {
    this.addEquipments = 2;
  }

  EquipmentsListClick() {
    this.addEquipments = 1;
  }

  getUnits(id: any) {
  }

  getSchedule(id: any) {
    let type = "unit";
    this.authService.getSchedule(type, id)
      .subscribe((result) => {
        let schedule = (result  as any).schedule;
        this.events = [];
        for (let i = 0; i < schedule.length; i++) {
          this.events.push({
            "title": schedule[i].userLabId.labName,
            "start": new Date(schedule[i].startTime).toJSON(),
            "end": new Date(schedule[i].endTime).toJSON()
          });
        }
      });
  }
  search(): void {
    let term = this.searchTerm;
    this.equipments = this.equipmentsCopy.filter(function (tag : any) {
      return tag.equipmentName.toLowerCase().indexOf(term.toLowerCase()) >= 0 || tag.type.toLowerCase().indexOf(term.toLowerCase()) >= 0
        || tag.equipmentType.toLowerCase().indexOf(term.toLowerCase()) >= 0 || tag.lab.labName.toLowerCase().indexOf(term.toLowerCase()) >= 0 ;
    });
  }

  UpdateEquipment(equipment: any)
  {
    this.authService.updateEquipment(equipment)
      .subscribe((result) => {
        if((result as any).status === "Success"){
          alert("Updated Succesfull");
        }
        else {
          alert("Updated aborted");
        }
      });
  }
}
