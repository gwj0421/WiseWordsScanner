import {httpClientForCredentials} from '../../index';

const checkAuth = async (setLoggedIn) => {
    try {
        const response = await httpClientForCredentials.get('/api/user/auth');
        setLoggedIn(true);
        return response.data;
    } catch (error) {
        window.location.replace('/login');
    }
};

export default checkAuth;
