import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { INoteBook } from 'app/shared/model/note-book.model';
import { getEntities as getNoteBooks } from 'app/entities/note-book/note-book.reducer';
import { INote } from 'app/shared/model/note.model';
import { getEntity, updateEntity, createEntity, reset } from './note.reducer';

export const NoteUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const noteBooks = useAppSelector(state => state.noteBook.entities);
  const noteEntity = useAppSelector(state => state.note.entity);
  const loading = useAppSelector(state => state.note.loading);
  const updating = useAppSelector(state => state.note.updating);
  const updateSuccess = useAppSelector(state => state.note.updateSuccess);

  const handleClose = () => {
    navigate('/note');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getNoteBooks({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.date = convertDateTimeToServer(values.date);

    const entity = {
      ...noteEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user.toString()),
      notebook: noteBooks.find(it => it.id.toString() === values.notebook.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          date: displayDefaultDateTime(),
        }
      : {
          ...noteEntity,
          date: convertDateTimeFromServer(noteEntity.date),
          user: noteEntity?.user?.id,
          notebook: noteEntity?.notebook?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="notebookApp.note.home.createOrEditLabel" data-cy="NoteCreateUpdateHeading">
            <Translate contentKey="notebookApp.note.home.createOrEditLabel">Create or edit a Note</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="note-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('notebookApp.note.title')}
                id="note-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('notebookApp.note.content')}
                id="note-content"
                name="content"
                data-cy="content"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('notebookApp.note.date')}
                id="note-date"
                name="date"
                data-cy="date"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField id="note-user" name="user" data-cy="user" label={translate('notebookApp.note.user')} type="select">
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="note-notebook"
                name="notebook"
                data-cy="notebook"
                label={translate('notebookApp.note.notebook')}
                type="select"
              >
                <option value="" key="0" />
                {noteBooks
                  ? noteBooks.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/note" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default NoteUpdate;
