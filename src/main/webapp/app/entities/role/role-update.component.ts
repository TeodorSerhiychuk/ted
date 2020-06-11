import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IRole, Role } from 'app/shared/model/role.model';
import { RoleService } from './role.service';
import { IActor } from 'app/shared/model/actor.model';
import { ActorService } from 'app/entities/actor/actor.service';

@Component({
  selector: 'jhi-role-update',
  templateUrl: './role-update.component.html'
})
export class RoleUpdateComponent implements OnInit {
  isSaving = false;
  actors: IActor[] = [];

  editForm = this.fb.group({
    id: [],
    characterName: [],
    characterDescription: [],
    actor: []
  });

  constructor(
    protected roleService: RoleService,
    protected actorService: ActorService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ role }) => {
      this.updateForm(role);

      this.actorService.query().subscribe((res: HttpResponse<IActor[]>) => (this.actors = res.body || []));
    });
  }

  updateForm(role: IRole): void {
    this.editForm.patchValue({
      id: role.id,
      characterName: role.characterName,
      characterDescription: role.characterDescription,
      actor: role.actor
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const role = this.createFromForm();
    if (role.id !== undefined) {
      this.subscribeToSaveResponse(this.roleService.update(role));
    } else {
      this.subscribeToSaveResponse(this.roleService.create(role));
    }
  }

  private createFromForm(): IRole {
    return {
      ...new Role(),
      id: this.editForm.get(['id'])!.value,
      characterName: this.editForm.get(['characterName'])!.value,
      characterDescription: this.editForm.get(['characterDescription'])!.value,
      actor: this.editForm.get(['actor'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRole>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IActor): any {
    return item.id;
  }
}
