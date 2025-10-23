document.addEventListener('DOMContentLoaded', () => {
    const themeSelector = document.getElementById('theme-selector');
    const themeLink = document.createElement('link');
    themeLink.rel = 'stylesheet';
    document.head.appendChild(themeLink);

    const setTheme = (theme) => {
        if (theme && theme !== 'default') {
            themeLink.href = `/css/themes/theme-${theme}.css`;
        } else {
            themeLink.href = '';
        }
        localStorage.setItem('theme', theme || 'default');
    };

    const loadTheme = () => {
        const selectedTheme = localStorage.getItem('theme') || 'default';
        setTheme(selectedTheme);
        if (themeSelector) {
            themeSelector.value = selectedTheme;
        }
    };

    if (themeSelector) {
        themeSelector.addEventListener('change', () => {
            setTheme(themeSelector.value);
        });
    }

    loadTheme();
});
document.addEventListener('DOMContentLoaded', () => {

    // --- 1. Existing Theme Switcher Logic ---
    const themeSelector = document.getElementById('theme-selector');
    const themeLink = document.createElement('link');
    themeLink.rel = 'stylesheet';
    document.head.appendChild(themeLink);

    const setTheme = (theme) => {
        if (theme && theme !== 'default') {
            // Updated path assuming themes are in /css/themes/
            themeLink.href = `/css/themes/theme-${theme}.css`;
        } else {
            themeLink.href = '';
        }
        localStorage.setItem('theme', theme || 'default');
    };

    const loadTheme = () => {
        const selectedTheme = localStorage.getItem('theme') || 'default';
        setTheme(selectedTheme);
        if (themeSelector) {
            themeSelector.value = selectedTheme;
        }
    };

    if (themeSelector) {
        themeSelector.addEventListener('change', () => {
            setTheme(themeSelector.value);
        });
    }

    loadTheme();

    // --- 2. New Custom UI Functionality ---
    // We call a new function to initialize the cursor and context menu
    initCustomUI();
});
document.addEventListener('DOMContentLoaded', () => {
    const themeSelector = document.getElementById('theme-selector');
    const themeLink = document.createElement('link');
    themeLink.rel = 'stylesheet';
    document.head.appendChild(themeLink);

    const setTheme = (theme) => {
        if (theme && theme !== 'default') {
            themeLink.href = `/css/themes/theme-${theme}.css`;
        } else {
            themeLink.href = '';
        }
        localStorage.setItem('theme', theme || 'default');
    };

    const loadTheme = () => {
        const selectedTheme = localStorage.getItem('theme') || 'default';
        setTheme(selectedTheme);
        if (themeSelector) {
            themeSelector.value = selectedTheme;
        }
    };

    if (themeSelector) {
        themeSelector.addEventListener('change', () => {
            setTheme(themeSelector.value);
        });
    }

    loadTheme();
    initCustomUI();
});

function initCustomUI() {
    const styles = `
        /* Custom Cursor Styles */
        body {
            cursor: none !important;
        }

        /* We hide the follower circle as it's not needed for the arrow cursor */
        #custom-cursor-follower {
            display: none;
        }

        /* We style the "dot" to be your new arrow cursor */
        #custom-cursor-dot {
            position: fixed;
            left: 0;
            top: 0;
            pointer-events: none; /* Lets clicks "pass through" */
            z-index: 99999; /* Stays on top */
            background-color: transparent;
            border-radius: 0;
            transition: none; /* We want instant, 1:1 mouse movement */
            width: 28px;
            height: 28px;
            background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 28 28"><path fill="white" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" d="M2.75 3.91L25.3 14.12L15.86 16.14L10.08 25.96L2.75 3.91Z"/></svg>');
            background-size: contain;
            background-repeat: no-repeat;
            background-position: center;
            color: var(--primary-accent-color, #009d63);
            transform: translate(0, 0); 
        }

        /* Hover state - outlined cursor */
        #custom-cursor-dot.hover {
            background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 28 28"><path fill="none" stroke="white" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" d="M2.75 3.91L25.3 14.12L15.86 16.14L10.08 25.96L2.75 3.91Z"/></svg>');
        }
        
        /* Make sure interactive elements also hide the default cursor */
        a, button, .btn, .nav-link, select, textarea, input, label, [role="button"], .context-menu-item {
            cursor: none !important;
        }

        * {
            cursor: none !important;
        }

        /* Custom Context Menu Styles */
        #custom-context-menu {
            position: fixed;
            display: none;
            background: var(--primary-element-bg, #1e1e2f);
            border: 1px solid var(--input-border-color, #404258);
            border-radius: 8px;
            box-shadow: var(--box-shadow, 0 4px 25px rgba(0, 0, 0, 0.3));
            z-index: 10000;
            min-width: 200px;
            overflow: hidden;
            padding: 5px 0;
        }
        .context-menu-item {
            padding: 10px 15px;
            color: var(--main-text-color, #ffffff);
            cursor: none !important;
            display: flex;
            align-items: center;
            gap: 10px;
            transition: background-color 0.2s ease;
            font-size: 0.9rem;
        }
        .context-menu-item i.fas {
            width: 16px;
            text-align: center;
            color: var(--sidebar-text-color, #c0c5cc);
        }
        .context-menu-item:hover {
            background-color: var(--primary-accent-color, #009d63);
            color: #fff;
        }
        .context-menu-item:hover i.fas {
            color: #fff;
        }
        .context-menu-separator {
            height: 1px;
            background-color: var(--input-border-color, #404258);
            margin: 5px 0;
        }
    `;

    const styleSheet = document.createElement("style");
    styleSheet.type = "text/css";
    styleSheet.innerText = styles;
    document.head.appendChild(styleSheet);

    // Create custom cursor
    const cursorDot = document.createElement('div');
    cursorDot.id = 'custom-cursor-dot';
    document.body.appendChild(cursorDot);

    // Track mouse movement
    document.addEventListener('mousemove', (e) => {
        cursorDot.style.left = `${e.clientX}px`;
        cursorDot.style.top = `${e.clientY}px`;
    });

    // Detect hoverable elements and change cursor style
    const hoverableSelectors = 'a, button, .btn, .nav-link, select, textarea, input, label, [role="button"], .context-menu-item';

    document.addEventListener('mouseover', (e) => {
        if (e.target.matches(hoverableSelectors) || e.target.closest(hoverableSelectors)) {
            cursorDot.classList.add('hover');
        }
    });

    document.addEventListener('mouseout', (e) => {
        if (e.target.matches(hoverableSelectors) || e.target.closest(hoverableSelectors)) {
            cursorDot.classList.remove('hover');
        }
    });

    // Custom Context Menu Logic
    const contextMenu = document.createElement('div');
    contextMenu.id = 'custom-context-menu';

    const isProfilePage = window.location.pathname.includes('/profile');
    const isDashboardPage = window.location.pathname.includes('/dashboard');

    contextMenu.innerHTML = `
        <div class="context-menu-item" data-action="back">
            <i class="fas fa-arrow-left"></i> Go Back
        </div>
        <div class="context-menu-item" data-action="reload">
            <i class="fas fa-redo"></i> Reload Page
        </div>
        <div class="context-menu-separator"></div>
        ${!isDashboardPage ? `
        <div class="context-menu-item" data-action="dashboard">
            <i class="fas fa-tachometer-alt"></i> Go to Dashboard
        </div>` : ''}
        ${!isProfilePage ? `
        <div class="context-menu-item" data-action="profile">
            <i class="fas fa-user-circle"></i> View Profile
        </div>` : ''}
        <div class="context-menu-item" data-action="logout">
            <i class="fas fa-sign-out-alt"></i> Logout
        </div>
    `;
    document.body.appendChild(contextMenu);

    document.addEventListener('contextmenu', (e) => {
        e.preventDefault();

        const { clientX: mouseX, clientY: mouseY } = e;
        const { innerWidth: winWidth, innerHeight: winHeight } = window;
        const { offsetWidth: menuWidth, offsetHeight: menuHeight } = contextMenu;

        const x = mouseX + menuWidth > winWidth ? winWidth - menuWidth - 10 : mouseX;
        const y = mouseY + menuHeight > winHeight ? winHeight - menuHeight - 10 : mouseY;

        contextMenu.style.left = `${x}px`;
        contextMenu.style.top = `${y}px`;
        contextMenu.style.display = 'block';
    });

    document.addEventListener('click', () => {
        contextMenu.style.display = 'none';
    });

    contextMenu.addEventListener('click', (e) => {
        const item = e.target.closest('.context-menu-item');
        if (item) {
            const action = item.dataset.action;

            switch (action) {
                case 'back':
                    window.history.back();
                    break;
                case 'reload':
                    window.location.reload();
                    break;
                case 'profile':
                    const profileLink = document.querySelector('.sidebar-footer a[href*="profile"]');
                    if (profileLink) {
                        window.location.href = profileLink.href;
                    }
                    break;
                case 'dashboard':
                    const dashboardLink = document.querySelector('.sidebar-nav a[href*="dashboard"]');
                    if (dashboardLink) {
                        window.location.href = dashboardLink.href;
                    }
                    break;
                case 'logout':
                    const logoutLink = document.querySelector('.sidebar-footer a[href*="logout"]');
                    if (logoutLink) {
                        window.location.href = logoutLink.href;
                    }
                    break;
            }
        }
    });
}