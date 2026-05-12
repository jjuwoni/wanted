import Product from "../../components/products/Product";
import MainCSS from './Main.module.css';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState, useRef } from "react";
import { useSelector, useDispatch } from 'react-redux';
import { Navigate } from "react-router-dom";

import {
    callProductListAPI
} from '../../apis/ProductAPICalls'

function Main() {

    const navigate = useNavigate();

    const dispatch = useDispatch();
    const products = useSelector(state => state.productReducer); 
    const productList = products.data;

    const pageInfo = products.pageInfo;

    const [currentPage, setCurrentPage] = useState(1);

    const pageNumber = [];
    if(pageInfo){
        for(let i = 1; i <= pageInfo.pageEnd ; i++){
            pageNumber.push(i);
        }
    }

    useEffect(
        () => {
            dispatch(callProductListAPI({
                currentPage: currentPage
            }));            
        }
        ,[currentPage]
    );

    return (
        <>
            <div className={ MainCSS.productDiv }>
            { 
                Array.isArray(productList) && productList.map((product) => (<Product key={ product.productCode } product={ product } />))
            }
            </div>
            <div style={{ listStyleType: "none", display: "flex" }}>
                { Array.isArray(productList) &&
                <button 
                    onClick={() => setCurrentPage(currentPage - 1)} 
                    disabled={currentPage === 1}
                    className={ MainCSS.pagingBtn }
                >
                    &lt;
                </button>
                }
                {pageNumber.map((num) => (
                <li key={num} onClick={() => setCurrentPage(num)}>
                    <button
                        style={ currentPage === num ? {backgroundColor : 'orange' } : null}
                        className={ MainCSS.pagingBtn }
                    >
                        {num}
                    </button>
                </li>
                ))}
                { Array.isArray(productList) &&
                <button 
                    className={ MainCSS.pagingBtn }
                    onClick={() => setCurrentPage(currentPage + 1)} 
                    disabled={currentPage === pageInfo.pageEnd  || pageInfo.total == 0}
                >
                    &gt;
                </button>
                }
            </div>
        </>
    );
}

export default Main;