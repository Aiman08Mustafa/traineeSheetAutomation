import React, { useEffect, useState } from 'react';
import './App.css';
import LoginForm from './components/LoginForm';
import RegisterForm from './components/RegisterForm';
import Dashboard from './components/Dashboard';
import ManagerDashboard from './components/ManagerDashboard';
import {
  assignTemplateToTrainee,
  clearAuth,
  fetchTemplates,
  fetchTraineeTemplates,
  loadAuth,
  loginUser,
  registerUser,
  saveAuth,
  validateToken,
} from './services/authService';

function App() {
  const [isLogin, setIsLogin] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [authUser, setAuthUser] = useState(null);
  const [authError, setAuthError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isCheckingSession, setIsCheckingSession] = useState(true);
  const [needsTemplateSelection, setNeedsTemplateSelection] = useState(false);
  const [availableTemplates, setAvailableTemplates] = useState([]);
  const [selectedTraineeTemplateId, setSelectedTraineeTemplateId] = useState(null);
  const [isTemplateLoading, setIsTemplateLoading] = useState(false);
  const [templateSelectionMode, setTemplateSelectionMode] = useState('assigned');
  const [selectedTemplateOption, setSelectedTemplateOption] = useState('');

  useEffect(() => {
    let isMounted = true;
    const restoreSession = async () => {
      const existingAuth = loadAuth();
      if (!existingAuth?.token) {
        if (isMounted) setIsCheckingSession(false);
        return;
      }
      try {
        await validateToken(existingAuth.token);
        if (isMounted) {
          setAuthUser(existingAuth);
          setIsAuthenticated(true);
        }
      } catch {
        clearAuth();
      } finally {
        if (isMounted) setIsCheckingSession(false);
      }
    };
    restoreSession();
    return () => {
      isMounted = false;
    };
  }, []);

  const toggleAuthMode = () => {
    setIsLogin(!isLogin);
    setAuthError('');
  };

  const handleLogin = async (credentials) => {
    setIsSubmitting(true);
    setAuthError('');
    try {
      const response = await loginUser(credentials);
      saveAuth(response);
      setAuthUser(response);
      setIsAuthenticated(true);
    } catch (error) {
      setAuthError(error.message || 'Invalid email or password.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleRegister = async (formData) => {
    setIsSubmitting(true);
    setAuthError('');
    try {
      const response = await registerUser(formData);
      saveAuth(response);
      setAuthUser(response);
      setIsAuthenticated(true);
    } catch (error) {
      setAuthError(error.message || 'Unable to create account.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleLogout = () => {
    clearAuth();
    setAuthUser(null);
    setIsAuthenticated(false);
    setIsLogin(true);
    setAuthError('');
    setNeedsTemplateSelection(false);
    setAvailableTemplates([]);
    setSelectedTraineeTemplateId(null);
    setTemplateSelectionMode('assigned');
    setSelectedTemplateOption('');
  };

  useEffect(() => {
    let isMounted = true;
    const setupTraineeTemplate = async () => {
      if (!isAuthenticated || !authUser?.token || authUser?.role !== 'TRAINEE') return;
      if (selectedTraineeTemplateId) return;
      setIsTemplateLoading(true);
      setAuthError('');
      try {
        const traineeTemplates = await fetchTraineeTemplates(authUser.userId, authUser.token);
        if (!isMounted) return;
        if (traineeTemplates.length > 0) {
          const matchingSelection = authUser.selectedTraineeTemplateId
            ? traineeTemplates.find((item) => item.traineeTemplateId === authUser.selectedTraineeTemplateId)
            : null;
          const selected = matchingSelection || traineeTemplates[0];
          const next = { ...authUser, selectedTraineeTemplateId: selected.traineeTemplateId };
          setAuthUser(next);
          setSelectedTraineeTemplateId(selected.traineeTemplateId);
          saveAuth(next);
          setNeedsTemplateSelection(false);
        } else {
          const templates = await fetchTemplates(authUser.token);
          if (!isMounted) return;
          setTemplateSelectionMode('new');
          setAvailableTemplates(
            templates.map((item) => ({
              id: item.templateId,
              title: item.title,
              description: item.description,
            }))
          );
          setSelectedTemplateOption('');
          setNeedsTemplateSelection(true);
        }
      } catch (error) {
        if (isMounted) setAuthError(error.message || 'Unable to load trainee templates.');
      } finally {
        if (isMounted) setIsTemplateLoading(false);
      }
    };
    setupTraineeTemplate();
    return () => {
      isMounted = false;
    };
  }, [isAuthenticated, authUser, selectedTraineeTemplateId]);

  const handleTemplateSelect = async () => {
    if (!authUser?.token) return;
    if (!selectedTemplateOption) {
      setAuthError('Please select template to continue.');
      return;
    }

    const selectedId = Number(selectedTemplateOption);
    setIsTemplateLoading(true);
    setAuthError('');
    try {
      let nextSelectedTraineeTemplateId = selectedId;
      if (templateSelectionMode === 'new') {
        const assigned = await assignTemplateToTrainee({
          templateId: selectedId,
          traineeId: authUser.userId,
          token: authUser.token,
        });
        nextSelectedTraineeTemplateId = assigned.traineeTemplateId;
      }

      const next = { ...authUser, selectedTraineeTemplateId: nextSelectedTraineeTemplateId };
      setAuthUser(next);
      setSelectedTraineeTemplateId(nextSelectedTraineeTemplateId);
      saveAuth(next);
      setNeedsTemplateSelection(false);
    } catch (error) {
      setAuthError(error.message || 'Unable to assign template.');
    } finally {
      setIsTemplateLoading(false);
    }
  };

  if (isCheckingSession) {
    return <div className="app-container"><div className="auth-wrapper"><div className="auth-content centered-message"><h2 className="form-title">Checking session...</h2></div></div></div>;
  }

  if (isAuthenticated) {
    if (authUser?.role === 'MANAGER') {
      return <div className="app-container"><ManagerDashboard onLogout={handleLogout} user={authUser} /></div>;
    }
    if (authUser?.role === 'TRAINEE' && needsTemplateSelection) {
      return (
        <div className="app-container">
          <div className="auth-wrapper">
            <div className="auth-content">
              <h2 className="form-title">Choose Your Template</h2>
              <p className="form-subtitle">Please select template to continue.</p>
              {authError ? <p className="form-error">{authError}</p> : null}
              <select
                className="input-field"
                value={selectedTemplateOption}
                onChange={(e) => setSelectedTemplateOption(e.target.value)}
                disabled={isTemplateLoading}
              >
                <option value="">Select Template</option>
                {availableTemplates.map((template) => (
                  <option key={template.id} value={template.id}>
                    {template.title}
                  </option>
                ))}
              </select>
              <button className="submit-btn manager-submit" onClick={handleTemplateSelect} disabled={isTemplateLoading}>
                Continue
              </button>
            </div>
          </div>
        </div>
      );
    }
    return (
      <div className="app-container">
        <Dashboard onLogout={handleLogout} user={authUser} selectedTraineeTemplateId={selectedTraineeTemplateId} />
      </div>
    );
  }

  return (
    <div className="app-container">
      <div className="auth-wrapper">
        <div className="auth-content">
          {authError ? <p className="form-error">{authError}</p> : null}
          {isLogin ? (
            <LoginForm onSwitch={toggleAuthMode} onLogin={handleLogin} isSubmitting={isSubmitting} />
          ) : (
            <RegisterForm onSwitch={toggleAuthMode} onRegister={handleRegister} isSubmitting={isSubmitting} />
          )}
        </div>
        
        <div className="auth-sidebar">
          <div className="animated-image-container">
            <img 
              src="/pastel_fluid_art.png" 
              alt="Pastel Fluid Art" 
              className="animated-image"
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;
