import { NavLink } from 'react-router-dom';
import NavCSS from './Navbar.module.css';
import { decodeJwt } from '../../utils/tokenUtils';

function Navbar() {
	const isLogin = window.localStorage.getItem('accessToken');
	let decoded = null;

	if (isLogin !== undefined && isLogin !== null) {
		const temp = decodeJwt(window.localStorage.getItem('accessToken'));
		console.log(temp);
		decoded = temp.auth[0];
	}

	console.log('decoded ', decoded);
	return (
		<div className={NavCSS.NavbarDiv}>
			<ul className={NavCSS.NavlistUl}>
				<li>
					<NavLink to="/">모든 음식</NavLink>
				</li>
				<li>
					<NavLink to="/product/meal">식사</NavLink>
				</li>
				<li>
					<NavLink to="/product/dessert">디저트</NavLink>
				</li>
				<li>
					<NavLink to="/product/beverage">음료</NavLink>
				</li>
				{decoded === 'ROLE_ADMIN' && (
					<li>
						<NavLink to="/product-management">상품관리</NavLink>
					</li>
				)}
			</ul>
		</div>
	);
}

export default Navbar;
