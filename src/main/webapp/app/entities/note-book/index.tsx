import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import NoteBook from './note-book';
import NoteBookDetail from './note-book-detail';
import NoteBookUpdate from './note-book-update';
import NoteBookDeleteDialog from './note-book-delete-dialog';

const NoteBookRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<NoteBook />} />
    <Route path="new" element={<NoteBookUpdate />} />
    <Route path=":id">
      <Route index element={<NoteBookDetail />} />
      <Route path="edit" element={<NoteBookUpdate />} />
      <Route path="delete" element={<NoteBookDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default NoteBookRoutes;
