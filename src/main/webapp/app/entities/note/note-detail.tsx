import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './note.reducer';

export const NoteDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const noteEntity = useAppSelector(state => state.note.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="noteDetailsHeading">
          <Translate contentKey="notebookApp.note.detail.title">Note</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{noteEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="notebookApp.note.title">Title</Translate>
            </span>
          </dt>
          <dd>{noteEntity.title}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="notebookApp.note.content">Content</Translate>
            </span>
          </dt>
          <dd>{noteEntity.content}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="notebookApp.note.date">Date</Translate>
            </span>
          </dt>
          <dd>{noteEntity.date ? <TextFormat value={noteEntity.date} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="notebookApp.note.user">User</Translate>
          </dt>
          <dd>{noteEntity.user ? noteEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="notebookApp.note.notebook">Notebook</Translate>
          </dt>
          <dd>{noteEntity.notebook ? noteEntity.notebook.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/note" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/note/${noteEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default NoteDetail;
