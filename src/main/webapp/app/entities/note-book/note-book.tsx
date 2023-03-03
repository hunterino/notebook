import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { INoteBook } from 'app/shared/model/note-book.model';
import { getEntities } from './note-book.reducer';

export const NoteBook = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const noteBookList = useAppSelector(state => state.noteBook.entities);
  const loading = useAppSelector(state => state.noteBook.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="note-book-heading" data-cy="NoteBookHeading">
        <Translate contentKey="notebookApp.noteBook.home.title">Note Books</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="notebookApp.noteBook.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/note-book/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="notebookApp.noteBook.home.createLabel">Create new Note Book</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {noteBookList && noteBookList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="notebookApp.noteBook.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="notebookApp.noteBook.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="notebookApp.noteBook.handle">Handle</Translate>
                </th>
                <th>
                  <Translate contentKey="notebookApp.noteBook.user">User</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {noteBookList.map((noteBook, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/note-book/${noteBook.id}`} color="link" size="sm">
                      {noteBook.id}
                    </Button>
                  </td>
                  <td>{noteBook.name}</td>
                  <td>{noteBook.handle}</td>
                  <td>{noteBook.user ? noteBook.user.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/note-book/${noteBook.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/note-book/${noteBook.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/note-book/${noteBook.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="notebookApp.noteBook.home.notFound">No Note Books found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default NoteBook;
