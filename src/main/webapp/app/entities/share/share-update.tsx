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
import { INote } from 'app/shared/model/note.model';
import { getEntities as getNotes } from 'app/entities/note/note.reducer';
import { IShare } from 'app/shared/model/share.model';
import { getEntity, updateEntity, createEntity, reset } from './share.reducer';

export const ShareUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const notes = useAppSelector(state => state.note.entities);
  const shareEntity = useAppSelector(state => state.share.entity);
  const loading = useAppSelector(state => state.share.loading);
  const updating = useAppSelector(state => state.share.updating);
  const updateSuccess = useAppSelector(state => state.share.updateSuccess);

  const handleClose = () => {
    navigate('/share');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getNotes({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...shareEntity,
      ...values,
      author: users.find(it => it.id.toString() === values.author.toString()),
      withUser: users.find(it => it.id.toString() === values.withUser.toString()),
      sharing: notes.find(it => it.id.toString() === values.sharing.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...shareEntity,
          author: shareEntity?.author?.id,
          withUser: shareEntity?.withUser?.id,
          sharing: shareEntity?.sharing?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="notebookApp.share.home.createOrEditLabel" data-cy="ShareCreateUpdateHeading">
            <Translate contentKey="notebookApp.share.home.createOrEditLabel">Create or edit a Share</Translate>
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
                  id="share-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('notebookApp.share.invite')}
                id="share-invite"
                name="invite"
                data-cy="invite"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField id="share-author" name="author" data-cy="author" label={translate('notebookApp.share.author')} type="select">
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
                id="share-withUser"
                name="withUser"
                data-cy="withUser"
                label={translate('notebookApp.share.withUser')}
                type="select"
              >
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
                id="share-sharing"
                name="sharing"
                data-cy="sharing"
                label={translate('notebookApp.share.sharing')}
                type="select"
              >
                <option value="" key="0" />
                {notes
                  ? notes.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.title}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/share" replace color="info">
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

export default ShareUpdate;
