import {jwtDecode} from "jwt-decode";

export function decodeJwt(token) {

    if(token === null) return null;

    return jwtDecode(token);
};
