import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/note-book">
        <Translate contentKey="global.menu.entities.noteBook" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/note">
        <Translate contentKey="global.menu.entities.note" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/share">
        <Translate contentKey="global.menu.entities.share" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
