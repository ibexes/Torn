import './App.css';
import Navbar from "./components/pages/Navbar";
import {BrowserRouter as Router, Route, Switch} from "react-router-dom";
import {Helmet} from 'react-helmet';
import FactionContributions from "./components/faction/contributions/FactionContributions";
import OrganisedCrimes from "./components/faction/organisedcrimes/OrganisedCrimes";

function App() {
    return (
        <>
            <Helmet>
                <title>London</title>
            </Helmet>
            <Router>
                <Navbar/>
                <div className="main-container">
                    <Switch>
                        <Route path="/" exact component={FactionContributions}/>
                        <Route path="/oc" exact component={OrganisedCrimes}/>
                    </Switch>
                </div>
            </Router>
        </>
    );
}

export default App;
