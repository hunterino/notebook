import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './note-book.reducer';

export const NoteBookDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const noteBookEntity = useAppSelector(state => state.noteBook.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="noteBookDetailsHeading">
          <Translate contentKey="notebookApp.noteBook.detail.title">NoteBook</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{noteBookEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="notebookApp.noteBook.name">Name</Translate>
            </span>
          </dt>
          <dd>{noteBookEntity.name}</dd>
          <dt>
            <span id="handle">
              <Translate contentKey="notebookApp.noteBook.handle">Handle</Translate>
            </span>
          </dt>
          <dd>{noteBookEntity.handle}</dd>
          <dt>
            <Translate contentKey="notebookApp.noteBook.user">User</Translate>
          </dt>
          <dd>{noteBookEntity.user ? noteBookEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/note-book" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/note-book/${noteBookEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default NoteBookDetail;
