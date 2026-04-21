import React, { useCallback, useEffect, useState } from 'react';
import {
  createModule,
  createTemplate,
  createTopic,
  deleteModule,
  deleteTopic,
  fetchModulesByTemplate,
  fetchServiceLines,
  fetchTemplates,
  fetchTopicsByModule,
  updateModule,
  updateTopic,
} from '../services/authService';

function ManagerDashboard({ user, onLogout }) {
  const [templates, setTemplates] = useState([]);
  const [serviceLines, setServiceLines] = useState([]);
  const [selectedTemplateId, setSelectedTemplateId] = useState('');
  const [modules, setModules] = useState([]);
  const [topicsByModule, setTopicsByModule] = useState({});
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Left panel view: 'template' | 'module' | 'topic'
  const [leftView, setLeftView] = useState('template');

  const [templateForm, setTemplateForm] = useState({ title: '', description: '', serviceLineId: '' });
  const [moduleForm, setModuleForm] = useState({ templateId: '', moduleName: '', description: '', sequenceOrder: 1 });
  const [topicForm, setTopicForm] = useState({ moduleId: '', topicName: '', learningObjective: '', readingMaterial: '', assignment: '', sequenceOrder: 1 });

  const [editingModuleId, setEditingModuleId] = useState(null);
  const [moduleEditForm, setModuleEditForm] = useState({ moduleName: '', description: '', sequenceOrder: 1, templateId: '' });
  const [editingTopicId, setEditingTopicId] = useState(null);
  const [topicEditForm, setTopicEditForm] = useState({ moduleId: '', topicName: '', learningObjective: '', readingMaterial: '', assignment: '', sequenceOrder: 1 });

  const [expandedModules, setExpandedModules] = useState({});

  const loadModules = useCallback(async (templateId) => {
    if (!templateId) return;
    const mod = await fetchModulesByTemplate(templateId, user.token);
    setModules(mod);
    const map = {};
    for (const m of mod) {
      map[m.moduleId] = await fetchTopicsByModule(m.moduleId, user.token);
    }
    setTopicsByModule(map);
  }, [user.token]);

  useEffect(() => {
    const load = async () => {
      try {
        const [tpls, lines] = await Promise.all([fetchTemplates(user.token), fetchServiceLines(user.token)]);
        setTemplates(tpls);
        setServiceLines(lines);
        if (tpls[0]) {
          setSelectedTemplateId(tpls[0].templateId);
          setModuleForm((p) => ({ ...p, templateId: tpls[0].templateId }));
          await loadModules(tpls[0].templateId);
        }
      } catch (e) {
        setError(e.message);
      }
    };
    load();
  }, [loadModules, user.token]);

  const clearMessages = () => { setError(''); setSuccess(''); };

  const submitTemplate = async (e) => {
    e.preventDefault();
    clearMessages();
    try {
      await createTemplate({ ...templateForm, createdBy: user.userId, serviceLineId: Number(templateForm.serviceLineId) }, user.token);
      setSuccess('Template created successfully.');
      setTemplateForm({ title: '', description: '', serviceLineId: '' });
      const tpls = await fetchTemplates(user.token);
      setTemplates(tpls);
    } catch (err) { setError(err.message); }
  };

  const submitModule = async (e) => {
    e.preventDefault();
    clearMessages();
    try {
      await createModule({ ...moduleForm, templateId: Number(moduleForm.templateId), sequenceOrder: Number(moduleForm.sequenceOrder) }, user.token);
      setSuccess('Module created successfully.');
      setModuleForm((p) => ({ ...p, moduleName: '', description: '', sequenceOrder: 1 }));
      await loadModules(moduleForm.templateId);
    } catch (err) { setError(err.message); }
  };

  const submitTopic = async (e) => {
    e.preventDefault();
    clearMessages();
    try {
      await createTopic({ ...topicForm, moduleId: Number(topicForm.moduleId), sequenceOrder: Number(topicForm.sequenceOrder) }, user.token);
      setSuccess('Topic created successfully.');
      setTopicForm((p) => ({ ...p, topicName: '', learningObjective: '', readingMaterial: '', assignment: '', sequenceOrder: 1 }));
      await loadModules(selectedTemplateId);
    } catch (err) { setError(err.message); }
  };

  const startEditModule = (module) => {
    setEditingModuleId(module.moduleId);
    setModuleEditForm({ templateId: module.templateId, moduleName: module.moduleName || '', description: module.description || '', sequenceOrder: module.sequenceOrder || 1 });
  };

  const saveModuleEdit = async (moduleId) => {
    const sequenceOrder = Number(moduleEditForm.sequenceOrder);
    if (!Number.isInteger(sequenceOrder) || sequenceOrder < 1) { setError('Sequence must be a positive integer.'); return; }
    clearMessages();
    try {
      await updateModule(moduleId, { templateId: Number(moduleEditForm.templateId), moduleName: moduleEditForm.moduleName.trim(), description: moduleEditForm.description.trim(), sequenceOrder }, user.token);
      setSuccess('Module updated.');
      setEditingModuleId(null);
      await loadModules(Number(selectedTemplateId));
    } catch (err) { setError(err.message); }
  };

  const handleDeleteModule = async (moduleId) => {
    if (!window.confirm('Delete this module? Its topics will be deleted too.')) return;
    clearMessages();
    try {
      await deleteModule(moduleId, user.token);
      setSuccess('Module deleted.');
      await loadModules(Number(selectedTemplateId));
    } catch (err) { setError(err.message); }
  };

  const startEditTopic = (topic, moduleId) => {
    setEditingTopicId(topic.topicId);
    setTopicEditForm({ moduleId, topicName: topic.topicName || '', learningObjective: topic.learningObjectives || '', readingMaterial: topic.readingMaterial || '', assignment: topic.assignment || '', sequenceOrder: topic.sequenceOrder || 1 });
  };

  const saveTopicEdit = async (topicId) => {
    const sequenceOrder = Number(topicEditForm.sequenceOrder);
    if (!Number.isInteger(sequenceOrder) || sequenceOrder < 1) { setError('Sequence must be a positive integer.'); return; }
    clearMessages();
    try {
      await updateTopic(topicId, { moduleId: Number(topicEditForm.moduleId), topicName: topicEditForm.topicName.trim(), learningObjectives: topicEditForm.learningObjective.trim(), readingMaterial: topicEditForm.readingMaterial.trim(), assignment: topicEditForm.assignment.trim(), sequenceOrder }, user.token);
      setSuccess('Topic updated.');
      setEditingTopicId(null);
      await loadModules(Number(selectedTemplateId));
    } catch (err) { setError(err.message); }
  };

  const handleDeleteTopic = async (topicId) => {
    if (!window.confirm('Delete this topic?')) return;
    clearMessages();
    try {
      await deleteTopic(topicId, user.token);
      setSuccess('Topic deleted.');
      await loadModules(Number(selectedTemplateId));
    } catch (err) { setError(err.message); }
  };

  const toggleModuleExpand = (moduleId) => {
    setExpandedModules((prev) => ({ ...prev, [moduleId]: !prev[moduleId] }));
  };

  const managerName = user?.name || user?.firstName || 'Manager';

  const navItems = [
    { key: 'template', label: 'Create Template', icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="3" y="3" width="18" height="18" rx="3"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/>
      </svg>
    )},
    { key: 'module', label: 'Add Module', icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
      </svg>
    )},
    { key: 'topic', label: 'Add Topic', icon: (
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/>
        <line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/>
      </svg>
    )},
  ];

  return (
    <div className="nd-shell">
      {/* TOP NAVBAR */}
      <nav className="nd-navbar">
        <div className="nd-navbar-logo">
          <div className="nd-logo-icon">
            <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg" width="28" height="28">
              <rect width="32" height="32" rx="8" fill="#7C6BF8"/>
              <path d="M8 22L13 10L18 18L21 14L24 22H8Z" fill="white" opacity="0.9"/>
              <circle cx="21" cy="11" r="3" fill="#C4B8FF"/>
            </svg>
          </div>
          <span className="nd-logo-text">Contour Software</span>
        </div>
        <div className="nd-navbar-right">
          <button className="nd-logout-btn" onClick={onLogout}>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
              <polyline points="16 17 21 12 16 7"/>
              <line x1="21" y1="12" x2="9" y2="12"/>
            </svg>
            Log Out
          </button>
        </div>
      </nav>

      <div className="nd-body">
        {/* LEFT PANEL — Manager Nav */}
        <aside className="nd-left-panel">
          <div className="nd-user-greeting">
            <h2 className="nd-greeting-name">{managerName}'s Dashboard</h2>
            <p className="nd-greeting-sub">Manager Portal</p>
          </div>

          <nav className="nd-manager-nav">
            {navItems.map((item) => (
              <button
                key={item.key}
                className={`nd-manager-nav-btn ${leftView === item.key ? 'active' : ''}`}
                onClick={() => setLeftView(item.key)}
              >
                {item.icon}
                <span>{item.label}</span>
              </button>
            ))}
          </nav>

          {/* Template selector for right panel */}
          <div className="nd-template-selector">
            <label className="nd-field-label">View Template Structure</label>
            <select
              className="nd-select"
              value={selectedTemplateId}
              onChange={async (e) => {
                setSelectedTemplateId(Number(e.target.value));
                await loadModules(Number(e.target.value));
              }}
            >
              {templates.map((t) => (
                <option key={t.templateId} value={t.templateId}>{t.title}</option>
              ))}
            </select>
          </div>
        </aside>

        {/* RIGHT PANEL */}
        <main className="nd-right-panel nd-manager-right">
          {/* Notifications */}
          {error && <div className="nd-alert nd-alert-error">{error}<button onClick={() => setError('')} className="nd-alert-close">×</button></div>}
          {success && <div className="nd-alert nd-alert-success">{success}<button onClick={() => setSuccess('')} className="nd-alert-close">×</button></div>}

          {/* FORM PANEL */}
          {leftView === 'template' && (
            <div className="nd-form-section">
              <div className="nd-content-header">
                <h2 className="nd-content-title">Create Template</h2>
                <p className="nd-content-meta">Define a new training template with a service line</p>
              </div>
              <form onSubmit={submitTemplate} className="nd-form">
                <div className="nd-field">
                  <label className="nd-field-label">Template Name</label>
                  <input className="nd-input" placeholder="e.g. Java, DotNet etc." value={templateForm.title} onChange={(e) => setTemplateForm((p) => ({ ...p, title: e.target.value }))} required/>
                </div>
                <div className="nd-field">
                  <label className="nd-field-label">Description</label>
                  <textarea className="nd-input nd-textarea" placeholder="Brief description..." value={templateForm.description} onChange={(e) => setTemplateForm((p) => ({ ...p, description: e.target.value }))}/>
                </div>
                <div className="nd-field">
                  <label className="nd-field-label">Service Line</label>
                  <select className="nd-select" value={templateForm.serviceLineId} onChange={(e) => setTemplateForm((p) => ({ ...p, serviceLineId: e.target.value }))}>
                    <option value="">Select Service Line</option>
                    {serviceLines.map((s) => <option key={s.serviceLineId} value={s.serviceLineId}>{s.department}</option>)}
                  </select>
                </div>
                <button className="nd-primary-btn" type="submit">Create Template</button>
              </form>
            </div>
          )}

          {leftView === 'module' && (
            <div className="nd-form-section">
              <div className="nd-content-header">
                <h2 className="nd-content-title">Add Module</h2>
                <p className="nd-content-meta">Modules can only be added to an existing template</p>
              </div>
              <form onSubmit={submitModule} className="nd-form">
                <div className="nd-field">
                  <label className="nd-field-label">Select Template</label>
                  <select className="nd-select" value={moduleForm.templateId} onChange={(e) => setModuleForm((p) => ({ ...p, templateId: e.target.value }))} required>
                    <option value="">Select Template</option>
                    {templates.map((t) => <option key={t.templateId} value={t.templateId}>{t.title}</option>)}
                  </select>
                </div>
                <div className="nd-field">
                  <label className="nd-field-label">Module Name</label>
                  <input className="nd-input" placeholder="e.g. Introduction to Systems" value={moduleForm.moduleName} onChange={(e) => setModuleForm((p) => ({ ...p, moduleName: e.target.value }))} required/>
                </div>
                <div className="nd-field">
                  <label className="nd-field-label">Description</label>
                  <textarea className="nd-input nd-textarea" placeholder="What will this module cover?" value={moduleForm.description} onChange={(e) => setModuleForm((p) => ({ ...p, description: e.target.value }))}/>
                </div>
                <div className="nd-field">
                  <label className="nd-field-label">Sequence Order</label>
                  <input className="nd-input" type="number" min="1" value={moduleForm.sequenceOrder} onChange={(e) => setModuleForm((p) => ({ ...p, sequenceOrder: e.target.value }))}/>
                </div>
                <button className="nd-primary-btn" type="submit">Add Module</button>
              </form>
            </div>
          )}

          {leftView === 'topic' && (
            <div className="nd-form-section">
              <div className="nd-content-header">
                <h2 className="nd-content-title">Add Topic</h2>
                <p className="nd-content-meta">Topics are added to existing modules</p>
              </div>
              <form onSubmit={submitTopic} className="nd-form">
                <div className="nd-field">
                  <label className="nd-field-label">Select Module</label>
                  <select className="nd-select" value={topicForm.moduleId} onChange={(e) => setTopicForm((p) => ({ ...p, moduleId: e.target.value }))} required>
                    <option value="">Select Module</option>
                    {modules.map((m) => <option key={m.moduleId} value={m.moduleId}>{m.moduleName}</option>)}
                  </select>
                </div>
                <div className="nd-field">
                  <label className="nd-field-label">Topic Name</label>
                  <input className="nd-input" placeholder="Topic title" value={topicForm.topicName} onChange={(e) => setTopicForm((p) => ({ ...p, topicName: e.target.value }))} required/>
                </div>
                <div className="nd-field">
                  <label className="nd-field-label">Learning Objective</label>
                  <textarea className="nd-input nd-textarea" placeholder="What will trainees learn?" value={topicForm.learningObjective} onChange={(e) => setTopicForm((p) => ({ ...p, learningObjective: e.target.value }))}/>
                </div>
                <div className="nd-field">
                  <label className="nd-field-label">Reading Material</label>
                  <textarea className="nd-input nd-textarea" placeholder="Links or resources..." value={topicForm.readingMaterial} onChange={(e) => setTopicForm((p) => ({ ...p, readingMaterial: e.target.value }))}/>
                </div>
                <div className="nd-field">
                  <label className="nd-field-label">Assignment</label>
                  <textarea className="nd-input nd-textarea" placeholder="Assignment details..." value={topicForm.assignment} onChange={(e) => setTopicForm((p) => ({ ...p, assignment: e.target.value }))}/>
                </div>
                <div className="nd-field">
                  <label className="nd-field-label">Sequence Order</label>
                  <input className="nd-input" type="number" min="1" value={topicForm.sequenceOrder} onChange={(e) => setTopicForm((p) => ({ ...p, sequenceOrder: e.target.value }))}/>
                </div>
                <button className="nd-primary-btn" type="submit">Add Topic</button>
              </form>
            </div>
          )}

          {/* TEMPLATE STRUCTURE */}
          <div className="nd-structure-section">
            <div className="nd-content-header" style={{ marginBottom: '16px' }}>
              <h3 className="nd-structure-title">Template Structure</h3>
              <span className="nd-content-meta">{modules.length} module{modules.length !== 1 ? 's' : ''}</span>
            </div>

            {modules.length === 0 && (
              <div className="nd-empty-state">
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#c4b8ff" strokeWidth="1.5"><rect x="3" y="3" width="18" height="18" rx="3"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/></svg>
                <p>No modules yet. Create a template and add modules.</p>
              </div>
            )}

            {modules.map((m) => (
              <div key={m.moduleId} className="nd-module-block">
                <div className="nd-module-row">
                  <button className="nd-module-toggle" onClick={() => toggleModuleExpand(m.moduleId)}>
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" style={{ transform: expandedModules[m.moduleId] ? 'rotate(90deg)' : 'rotate(0)', transition: 'transform 0.2s' }}>
                      <polyline points="9 18 15 12 9 6"/>
                    </svg>
                  </button>
                  <span className="nd-module-seq">{m.sequenceOrder}.</span>
                  {editingModuleId === m.moduleId ? (
                    <div className="nd-inline-edit">
                      <input className="nd-input nd-inline-input" value={moduleEditForm.moduleName} onChange={(e) => setModuleEditForm((p) => ({ ...p, moduleName: e.target.value }))} placeholder="Module name"/>
                      <textarea className="nd-input nd-textarea nd-inline-input" value={moduleEditForm.description} onChange={(e) => setModuleEditForm((p) => ({ ...p, description: e.target.value }))} placeholder="Description"/>
                      <input className="nd-input nd-inline-input" type="number" min="1" value={moduleEditForm.sequenceOrder} onChange={(e) => setModuleEditForm((p) => ({ ...p, sequenceOrder: e.target.value }))}/>
                      <div className="nd-inline-actions">
                        <button className="nd-tag-btn nd-tag-save" onClick={() => saveModuleEdit(m.moduleId)}>Save</button>
                        <button className="nd-tag-btn" onClick={() => setEditingModuleId(null)}>Cancel</button>
                      </div>
                    </div>
                  ) : (
                    <span className="nd-module-label">{m.moduleName}</span>
                  )}
                  <div className="nd-row-actions">
                    <button className="nd-tag-btn nd-tag-edit" onClick={() => startEditModule(m)}>Edit</button>
                    <button className="nd-tag-btn nd-tag-del" onClick={() => handleDeleteModule(m.moduleId)}>Delete</button>
                  </div>
                </div>

                {expandedModules[m.moduleId] && (
                  <div className="nd-topics-block">
                    {(topicsByModule[m.moduleId] || []).length === 0 && (
                      <p className="nd-no-topics">No topics yet.</p>
                    )}
                    {(topicsByModule[m.moduleId] || []).map((topic) => (
                      <div key={topic.topicId} className="nd-topic-row">
                        <span className="nd-topic-seq">{topic.sequenceOrder}.</span>
                        {editingTopicId === topic.topicId ? (
                          <div className="nd-inline-edit">
                            <input className="nd-input nd-inline-input" value={topicEditForm.topicName} onChange={(e) => setTopicEditForm((p) => ({ ...p, topicName: e.target.value }))} placeholder="Topic name"/>
                            <textarea className="nd-input nd-textarea nd-inline-input" value={topicEditForm.learningObjective} onChange={(e) => setTopicEditForm((p) => ({ ...p, learningObjective: e.target.value }))} placeholder="Objective"/>
                            <textarea className="nd-input nd-textarea nd-inline-input" value={topicEditForm.readingMaterial} onChange={(e) => setTopicEditForm((p) => ({ ...p, readingMaterial: e.target.value }))} placeholder="Reading material"/>
                            <textarea className="nd-input nd-textarea nd-inline-input" value={topicEditForm.assignment} onChange={(e) => setTopicEditForm((p) => ({ ...p, assignment: e.target.value }))} placeholder="Assignment"/>
                            <input className="nd-input nd-inline-input" type="number" min="1" value={topicEditForm.sequenceOrder} onChange={(e) => setTopicEditForm((p) => ({ ...p, sequenceOrder: e.target.value }))}/>
                            <div className="nd-inline-actions">
                              <button className="nd-tag-btn nd-tag-save" onClick={() => saveTopicEdit(topic.topicId)}>Save</button>
                              <button className="nd-tag-btn" onClick={() => setEditingTopicId(null)}>Cancel</button>
                            </div>
                          </div>
                        ) : (
                          <span className="nd-topic-label">{topic.topicName}</span>
                        )}
                        <div className="nd-row-actions">
                          <button className="nd-tag-btn nd-tag-edit" onClick={() => startEditTopic(topic, m.moduleId)}>Edit</button>
                          <button className="nd-tag-btn nd-tag-del" onClick={() => handleDeleteTopic(topic.topicId)}>Delete</button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        </main>
      </div>
    </div>
  );
}

export default ManagerDashboard;