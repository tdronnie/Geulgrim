import { Navigate, useRoutes } from 'react-router-dom';

import MainLayout from 'src/layouts/main';

// import { PATH_AFTER_LOGIN } from 'src/config-global';
import { nftRoutes } from './nft';
import { authRoutes } from './auth';
import { customRoutes } from './custom';
import { recruitRoutes } from './recruit';
import { authDemoRoutes } from './auth-demo';
import { HomePage, mainRoutes } from './main';
import { dashboardRoutes } from './dashboard';
import { communityRoutes } from './community';
import { componentsRoutes } from './components';

// ----------------------------------------------------------------------

export default function Router() {
  console.log('check');
  return useRoutes([
    // SET INDEX PAGE WITH SKIP HOME PAGE
    // {
    //   path: '/',
    //   element: <Navigate to={PATH_AFTER_LOGIN} replace />,
    // },

    // ----------------------------------------------------------------------

    // SET INDEX PAGE WITH HOME PAGE
    {
      path: '/',
      element: (
        <Navigate replace to="/recruit" />
      ),
    },


    // Auth routes
    ...authRoutes,
    ...authDemoRoutes,

    // Dashboard routes
    ...dashboardRoutes,

    // Main routes
    ...mainRoutes,

    // Components routes
    ...componentsRoutes,

    // Custom routes
    ...customRoutes,
    ...recruitRoutes,
    ...communityRoutes,
    ...nftRoutes,

    // No match 404
    { path: '*', element: <Navigate to="/404" replace /> },
  ]);
}