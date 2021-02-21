import React, {useState} from 'react';
import {Link} from "react-router-dom";
import {SiTransportforlondon} from "react-icons/all";
import {FaBars, FaTimes} from "react-icons/fa";
import './Navbar.css';

function Navbar() {
    const [click, setClick] = useState(false);
    const [button, setButton] = useState(true);

    const handleMenuClick = () => setClick(!click);
    const closeMobileMenu = () => setClick(false);

    const showMenu = () => {
        if(window.innerWidth <= 900) {
            setButton(false);
        } else {
            setButton(true);
        }
    };

    window.addEventListener('resize', showMenu);

    return (
        <>
            <div className="navbar">
                <div className="navbar-container container">
                    <Link to="/" className="navbar-logo" onClick={closeMobileMenu}>
                        <SiTransportforlondon className="navbar-icon"/>
                        LONDON
                    </Link>
                    <div className="menu-icon" onClick={handleMenuClick}>
                        {click ? <FaTimes/> : <FaBars/>}
                    </div>
                    <ul className={click? 'nav-menu active' : 'nav-menu'}>
                        <li className="nav-item">
                            <Link className="nav-links" to="/">
                                HOME
                            </Link>
                        </li>
                        <li className="nav-item">
                            <Link className="nav-links" to="/oc">
                                OC
                            </Link>
                        </li>
                        <li className="nav-item">
                            <a className="nav-links" href="/logout">
                                LOGOUT
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </>
    );
}

export default Navbar;