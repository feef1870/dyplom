import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

export interface ReportDialogData {
  title: string;
  label: string;
  initialValue?: string;
}

@Component({
  selector: 'app-report-dialog',
  imports: [MatDialogModule, MatButtonModule, MatFormFieldModule, MatInputModule, ReactiveFormsModule],
  templateUrl: './report-dialog.html',
  styleUrl: './report-dialog.scss',
})
export class ReportDialog {
  private readonly dialogRef = inject(MatDialogRef<ReportDialog>);
  private readonly fb = inject(FormBuilder);
  readonly data = inject<ReportDialogData>(MAT_DIALOG_DATA);

  form = this.fb.group({
    text: [this.data.initialValue ?? '', Validators.required],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.dialogRef.close(this.form.value.text!.trim());
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
