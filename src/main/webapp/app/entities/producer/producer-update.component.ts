import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IProducer, Producer } from 'app/shared/model/producer.model';
import { ProducerService } from './producer.service';

@Component({
  selector: 'jhi-producer-update',
  templateUrl: './producer-update.component.html'
})
export class ProducerUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.maxLength(100)]],
    surname: [null, [Validators.required, Validators.maxLength(100)]],
    bio: [],
    photoURL: []
  });

  constructor(protected producerService: ProducerService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ producer }) => {
      this.updateForm(producer);
    });
  }

  updateForm(producer: IProducer): void {
    this.editForm.patchValue({
      id: producer.id,
      name: producer.name,
      surname: producer.surname,
      bio: producer.bio,
      photoURL: producer.photoURL
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const producer = this.createFromForm();
    if (producer.id !== undefined) {
      this.subscribeToSaveResponse(this.producerService.update(producer));
    } else {
      this.subscribeToSaveResponse(this.producerService.create(producer));
    }
  }

  private createFromForm(): IProducer {
    return {
      ...new Producer(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      surname: this.editForm.get(['surname'])!.value,
      bio: this.editForm.get(['bio'])!.value,
      photoURL: this.editForm.get(['photoURL'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProducer>>): void {
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
}
