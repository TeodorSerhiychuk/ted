import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { ImdbTestModule } from '../../../test.module';
import { SeriesUpdateComponent } from 'app/entities/series/series-update.component';
import { SeriesService } from 'app/entities/series/series.service';
import { Series } from 'app/shared/model/series.model';

describe('Component Tests', () => {
  describe('Series Management Update Component', () => {
    let comp: SeriesUpdateComponent;
    let fixture: ComponentFixture<SeriesUpdateComponent>;
    let service: SeriesService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ImdbTestModule],
        declarations: [SeriesUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(SeriesUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SeriesUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SeriesService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Series(123);
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
        const entity = new Series();
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
