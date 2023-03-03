import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IShare } from 'app/shared/model/share.model';
import { getEntities } from './share.reducer';

export const Share = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const shareList = useAppSelector(state => state.share.entities);
  const loading = useAppSelector(state => state.share.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="share-heading" data-cy="ShareHeading">
        <Translate contentKey="notebookApp.share.home.title">Shares</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="notebookApp.share.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/share/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="notebookApp.share.home.createLabel">Create new Share</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {shareList && shareList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="notebookApp.share.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="notebookApp.share.invite">Invite</Translate>
                </th>
                <th>
                  <Translate contentKey="notebookApp.share.author">Author</Translate>
                </th>
                <th>
                  <Translate contentKey="notebookApp.share.withUser">With User</Translate>
                </th>
                <th>
                  <Translate contentKey="notebookApp.share.sharing">Sharing</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {shareList.map((share, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/share/${share.id}`} color="link" size="sm">
                      {share.id}
                    </Button>
                  </td>
                  <td>{share.invite}</td>
                  <td>{share.author ? share.author.login : ''}</td>
                  <td>{share.withUser ? share.withUser.login : ''}</td>
                  <td>{share.sharing ? <Link to={`/note/${share.sharing.id}`}>{share.sharing.title}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/share/${share.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/share/${share.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/share/${share.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="notebookApp.share.home.notFound">No Shares found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Share;
