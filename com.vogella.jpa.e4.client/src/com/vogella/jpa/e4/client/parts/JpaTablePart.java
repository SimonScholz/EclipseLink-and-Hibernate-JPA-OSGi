
package com.vogella.jpa.e4.client.parts;

import javax.annotation.PostConstruct;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.vogella.jpa.model.Todo;
import com.vogella.jpa.model.TodoService;

public class JpaTablePart {

	@PostConstruct
	public void postConstruct(Composite parent, TodoService todoService) {

		TableViewer viewer = new TableViewer(parent);
		viewer.getTable().setHeaderVisible(true);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		
		TableViewerColumn idColumn = new TableViewerColumn(viewer, SWT.NONE);
		idColumn.getColumn().setWidth(30);
		idColumn.getColumn().setText("ID");
		idColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof Todo) {
					return String.valueOf(((Todo) element).getId());
				}
				return super.getText(element);
			}
		});

		TableViewerColumn summaryColumn = new TableViewerColumn(viewer, SWT.NONE);
		summaryColumn.getColumn().setWidth(300);
		summaryColumn.getColumn().setText("SUMMARY");
		summaryColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof Todo) {
					return ((Todo) element).getSummary();
				}
				return super.getText(element);
			}
		});

		TableViewerColumn descriptionColumn = new TableViewerColumn(viewer, SWT.NONE);
		descriptionColumn.getColumn().setWidth(300);
		descriptionColumn.getColumn().setText("DESCRIPTION");
		descriptionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof Todo) {
					return ((Todo) element).getDescription();
				}
				return super.getText(element);
			}
		});
		
		todoService.getTodos(viewer::setInput);
	}

}
