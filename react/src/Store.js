import rootReducer from './modules';
import { composeWithDevTools} from '@redux-devtools/extension'
import { createStore, applyMiddleware } from 'redux';
import {thunk} from 'redux-thunk';
import { createLogger } from 'redux-logger';

/* 
    리액트를 경험해 본 적이 있는 사람들은
    redux-toolkit 을 사용해서
    리덕스 관련 소스코드를 변환하는 것을 도전해보시오.
*/
const logger = createLogger();

const store = createStore(
    rootReducer,
    composeWithDevTools(applyMiddleware(thunk , logger))
);

export default store;