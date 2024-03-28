import {httpClientForCheck, httpClientForCredentials} from "../../index";
import axios from "axios";

const checkAuthForUnknown = async (setLoggedIn) => {
    try {
        await httpClientForCheck.get("/api/user/auth").catch();
        setLoggedIn(true);
    } catch (error) {
        setLoggedIn(false);
    }
};

export default checkAuthForUnknown;
