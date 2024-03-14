// authUtils.js

import { httpClientForCredentials } from '../../index';

const checkAuth = async () => {
    await httpClientForCredentials.get('/api/user/auth');
};

export default checkAuth;
