import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { ImdbTestModule } from '../../../test.module';
import { ProducerUpdateComponent } from 'app/entities/producer/producer-update.component';
import { ProducerService } from 'app/entities/producer/producer.service';
import { Producer } from 'app/shared/model/producer.model';

describe('Component Tests', () => {
  describe('Producer Management Update Component', () => {
    let comp: ProducerUpdateComponent;
    let fixture: ComponentFixture<ProducerUpdateComponent>;
    let service: ProducerService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ImdbTestModule],
        declarations: [ProducerUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ProducerUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ProducerUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ProducerService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Producer(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Producer();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
